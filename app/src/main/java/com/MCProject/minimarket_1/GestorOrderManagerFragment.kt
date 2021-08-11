package com.MCProject.minimarket_1

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.ListFragment

/**
 * A fragment representing a list of Items.
 */
class GestorOrderManagerFragment : ListFragment() {

    private var columnCount = 1
    var myArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gestor_order_manager_list, container, false)

        // Set the adapter
        /*if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(DummyContent.ITEMS)
            }
        }*/

        return view
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            GestorOrderManagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    fun addDevice(device: BluetoothDevice?) {
        if(device != null) {

            if(device.name != null) {
                Log.i("HEY", "Adding Device: $device")
                Log.i("HEY", "Device Name: ${device.name}")

                if(!myArray.contains(device.name)) {
                    myArray.add(
                        device.name.toString()
                    )
                    listAdapter = ArrayAdapter(activity!!,android.R.layout.simple_list_item_1, myArray)
                }
            }
        }
    }
}