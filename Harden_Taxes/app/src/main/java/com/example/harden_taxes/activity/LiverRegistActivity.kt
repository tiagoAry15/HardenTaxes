package com.example.harden_taxes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.harden_taxes.R
import com.example.harden_taxes.model.Apartment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LiverRegistActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mFirstName: EditText
    private lateinit var mLastName: EditText
    private lateinit var mTelefone: EditText
    private lateinit var mEmail: EditText
    private lateinit var mSave: Button
    private lateinit var mApartId:String
    private lateinit var mFloor:String
    private lateinit var mDATA:String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liver_regist)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mFirstName = findViewById(R.id.LiverForm_editext_firstname)
        mLastName = findViewById(R.id.LiverForm_editext_lastname)
        mTelefone = findViewById(R.id.LiverForm_editext_telefone)
        mEmail = findViewById(R.id.LiverForm_editext_email)
        mSave = findViewById(R.id.LiverForm_button_save)
        mSave.setOnClickListener(this)
        mFloor = intent.getStringExtra("FloorNum")?: ""
        mApartId = intent.getStringExtra("ApId") ?: ""
        mDATA = intent.getStringExtra("data") ?: ""
        if(mApartId.isNotEmpty()){
            val query = mDatabase.reference.child("users/${mAuth.uid}/Condominio/andares/${mApartId}").orderByKey()
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val apartment = snapshot.getValue(Apartment::class.java)
                    handler.post{
                        mFirstName.text = Editable.Factory.getInstance().newEditable(apartment?.firstName)
                        mLastName.text = Editable.Factory.getInstance().newEditable(apartment?.lastName)
                        mEmail.text = Editable.Factory.getInstance().newEditable(apartment?.email)
                        mTelefone.text = Editable.Factory.getInstance().newEditable(apartment?.telefone)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onClick(v: View?) {
        val firstname = mFirstName.text.toString()
        val lastname = mLastName.text.toString()
        val telefone = mTelefone.text.toString()
        val email = mEmail.text.toString()
        var isFormFilled = true
        if (firstname.isEmpty()) {
            mFirstName.error = "este campo não pode estar vazio"
            isFormFilled = false
        }
        if (lastname.isEmpty()) {
            mLastName.error = "este campo não pode estar vazio"
            isFormFilled = false
        }


        if (telefone.isEmpty()) {
            mTelefone.error = "este campo não pode estar vazio"
            isFormFilled = false
        }

        if (email.isEmpty()) {
            mEmail.error = "este campo não pode estar vazio"
            isFormFilled = false
        }
        if (isFormFilled) {
            var taxa = ""
            val ref = mDatabase.getReference("/users/${mAuth.uid}/Condominio/andares/${mApartId}")
            val ref2 = mDatabase.reference.child("/users/${mAuth.uid}/Condominio/andares/${mApartId}/monthTax").get()
            ref2.addOnSuccessListener{
                taxa = it!!.value.toString()
                val Liver = Apartment(mApartId,firstname,lastname,email,telefone,mFloor.toInt(),monthTax = taxa, vencimento = mDATA)
                ref.setValue(Liver)
                finish()
            }



        }
    }
}