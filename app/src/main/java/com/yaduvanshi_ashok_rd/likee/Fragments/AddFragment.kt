package com.yaduvanshi_ashok_rd.likee.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yaduvanshi_ashok_rd.likee.Adapter.ReelAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Reel
import com.yaduvanshi_ashok_rd.likee.R


class AddFragment : DialogFragment() {
    private lateinit var reelList:ArrayList<Reel>
    private lateinit var reelRecyclerView:RecyclerView
    private lateinit var reelAdapter:ReelAdapter
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)


        return view
    }

    }


