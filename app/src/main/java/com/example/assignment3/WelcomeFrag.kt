package com.example.assignment3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class WelcomeFrag : Fragment() {

    private lateinit var btnSaveName: Button
    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)
        btnSaveName = view.findViewById(R.id.btnSaveName)
        editText = view.findViewById(R.id.editText)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSaveName.setOnClickListener {
            // Save the entered name, if null then saved name is aaaa
            val username: String = editText.text.toString().takeIf { it.isNotEmpty() } ?: "aaaa"

            val bundle = Bundle()
            bundle.putString("username", username)

            // Create an instance of the next fragment
            val gameFragment = GameFrag()

            // Set the bundle as arguments for the next fragment
            gameFragment.arguments = bundle

            // Replace the current fragment with the next fragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, gameFragment)
                .commit()
        }
    }
}