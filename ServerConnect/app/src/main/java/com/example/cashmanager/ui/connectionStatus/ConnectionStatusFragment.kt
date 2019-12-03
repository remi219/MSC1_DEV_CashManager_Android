package com.example.cashmanager.ui.connectionStatus

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.example.cashmanager.R
import com.example.cashmanager.service.StatusService
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ConnectionStatusFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ConnectionStatusFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ConnectionStatusFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val statusAPI : StatusService by inject()

    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var statusTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_status, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        statusTextView = view?.findViewById(R.id.status_textview)
        getServerStatus()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun getServerStatus() {
        val call = statusAPI.connectionStatus()
        statusTextView?.text = resources.getString(R.string.status_attempting_connection)
        statusTextView?.setBackgroundColor(
            ContextCompat.getColor(context!!, R.color.colorPending)
        )

        try {
            call.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    statusTextView?.text = resources.getString(R.string.status_connected)
                    statusTextView?.setBackgroundColor(
                        ContextCompat.getColor(context!!, R.color.colorSuccess)
                    )
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    try {
                        statusTextView?.text = resources.getString(R.string.status_disconnected)
                        statusTextView?.setBackgroundColor(
                            ContextCompat.getColor(context!!, R.color.colorFailure)
                        )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

            })
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(ex.message)
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ConnectionStatusFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ConnectionStatusFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
