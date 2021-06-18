
package com.example.harden_taxes.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harden_taxes.Adapter.ApartmentAdapter
import com.example.harden_taxes.R
import com.example.harden_taxes.model.Apartment
import com.example.harden_taxes.model.Condominium
import com.example.todolist.adapter.ApartmentListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.text.NumberFormat


class MainActivity : AppCompatActivity(), View.OnClickListener, ApartmentListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mApartmentRecycler: RecyclerView
    private lateinit var mRegistApartment: Button
    private lateinit var mDate: TextView
    private var apartamentos = 1
    private var email = ""
    private lateinit var mUserName: TextView
    private lateinit var apartmentAdapter: ApartmentAdapter
    private  val handler = Handler(Looper.getMainLooper())
    private lateinit var mTax: TextView
    private lateinit var mBuilding: TextView
    private lateinit var mTaxRegister: Button
    private var mApartmentList = mutableListOf<Apartment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRegistApartment = findViewById(R.id.main_button_register)
        mBuilding = findViewById(R.id.main_textview_building)
        mRegistApartment.setOnClickListener(this)
        mDate = findViewById(R.id.main_textview_date)
        mApartmentRecycler = findViewById(R.id.main_recyclerview_apartments)
        mUserName = findViewById(R.id.main_textview_firstname)
        mTaxRegister = findViewById(R.id.main_button_registTax)
        mTax = findViewById(R.id.main_textview_tax)
        mTaxRegister.setOnClickListener(this)


    }

    override fun onStart() {

        super.onStart()
        val dialog = ProgressDialog(this)
        dialog.setTitle("Carregando")
        dialog.isIndeterminate = true
        dialog.show()

        val UserName = mDatabase.reference.child("/users/${mAuth.uid!!}/firstName").get()
            .addOnSuccessListener {
                mUserName.text = ("Olá ${it.value}")
            }

             mDatabase.reference.child("/users/${mAuth.uid!!}/email").get()
            .addOnSuccessListener {
               email = it.value.toString()
            }

        val query = mDatabase.reference.child("/users/${mAuth.uid}/Condominio/andares").orderByKey()
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var expenseList = mutableListOf<Apartment>()
                snapshot.children.forEach{
                    val expense = it.getValue(Apartment::class.java)
                    expenseList.add(expense!!)

                }
                mApartmentList.clear()
                mApartmentList.addAll(expenseList)
                handler.post{

                    apartmentAdapter = ApartmentAdapter(mApartmentList)
                    if(apartmentAdapter.itemCount != 0){
                        mTaxRegister.isVisible = true
                        mRegistApartment.isVisible = false
                        val taxPath = mDatabase.reference.child("/users/${mAuth.uid}/Condominio")
                            .addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val tax = snapshot.getValue(Condominium::class.java)
                                    mTax.text = ("Montante mensal: ${NumberFormat.getCurrencyInstance().format(tax!!.tax)}")
                                    if(tax.tax != 0.0){
                                        mTaxRegister.text = ("Alterar Taxa Mensal")
                                    }
                                    mDate.text = ("Data de vencimento: ${tax.vencimento}")
                                    mBuilding.text = ("Edifício ${tax.name}")
                                  apartamentos = tax.apartments
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })


                    }

                    apartmentAdapter.setOnUserItemClickListener(this@MainActivity)
                    val llm = LinearLayoutManager(applicationContext)
                    mApartmentRecycler.apply {
                        adapter = apartmentAdapter
                        layoutManager = llm
                    }
                    dialog.dismiss()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.main_button_register ->{
                val it = Intent(applicationContext, RegistApartmentActivity::class.java)
                startActivity(it)
            }
            R.id.main_button_registTax ->{
                val it = Intent(applicationContext, CreateTaxActivity::class.java)
                it.putExtra("apart",apartamentos)
                startActivity(it)
            }
        }
    }

    override fun onClick(v: View, position: Int) {
        val it = Intent(applicationContext, DetailApartmentActivity::class.java)
        it.putExtra("ApId", mApartmentList[position].id)
        it.putExtra("email",email)
        startActivity(it)
    }

    override fun onLongClick(v: View, position: Int) {
        TODO("Not yet implemented")
    }
}
