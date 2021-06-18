package com.example.harden_taxes.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.harden_taxes.R
import com.example.harden_taxes.model.Apartment
import com.example.harden_taxes.model.Condominium
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat

class DetailApartmentActivity : AppCompatActivity(), View.OnClickListener {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var mApartId:String
    private lateinit var mFirstName: TextView
    private lateinit var mLastName: TextView
    private lateinit var mTelefone: TextView
    private lateinit var mEmail: TextView
    private lateinit var mFloor: TextView
    private lateinit var mSituation: TextView
    private lateinit var mTax: TextView
    private lateinit var mEdit: Button
    private lateinit var mPay: Button
    private lateinit var mDate: TextView
    private lateinit var mMessage: FloatingActionButton
    private lateinit var UserEmail:String
    private lateinit var data:String
    var andar = ""
    var taxaApartamento = 0.0
    var taxaTotal = 0.0
    var taxaAtual = 0.0
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        mDatabase = FirebaseDatabase.getInstance()
        mMessage = findViewById(R.id.detail_button_message)
        mAuth = FirebaseAuth.getInstance()
        mDate = findViewById(R.id.detail_textview_date)
        mFirstName = findViewById(R.id.Detail_textView_firstname)
        mLastName = findViewById(R.id.Detail_textView_lastname)
        mTelefone = findViewById(R.id.Detail_textView_telefone)
        mEmail = findViewById(R.id.Detail_textView_email)
        mFloor = findViewById(R.id.detail_textView_floor)
        mEdit = findViewById(R.id.detail_button_edit)
        mTax = findViewById(R.id.detail_textview_tax)
        mSituation = findViewById(R.id.detail_textview_situation)
        mPay = findViewById(R.id.detail_button_pay)
        mMessage.setOnClickListener(this)
        mPay.setOnClickListener(this)
        mEdit.setOnClickListener(this)

        mApartId = intent.getStringExtra("ApId") ?: ""
        UserEmail = intent.getStringExtra("email") ?: ""
        if(mApartId.isNotEmpty()){

            mDatabase.reference.child("users/${mAuth.uid}/Condominio").orderByKey()
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        taxaTotal = snapshot.getValue(Condominium::class.java)!!.tax

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            val query = mDatabase.reference.child("users/${mAuth.uid}/Condominio/andares/${mApartId}").orderByKey()
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val apartment = snapshot.getValue(Apartment::class.java)
                    handler.post{
                    mFirstName.text = Editable.Factory.getInstance().newEditable(apartment?.firstName)
                    mLastName.text =Editable.Factory.getInstance().newEditable(apartment?.lastName)





                    mEmail.text =Editable.Factory.getInstance().newEditable(apartment?.email)
                        if(apartment?.email != UserEmail){
                            mMessage.isVisible = true
                            mPay.isVisible = false
                        }
                        else{
                            mMessage.isVisible = false
                            mPay.isVisible = true
                        }
                    mTelefone.text = Editable.Factory.getInstance().newEditable(apartment?.telefone)
                        mFloor.text = Editable.Factory.getInstance().newEditable(apartment?.floor.toString())
                        mTax.text = Editable.Factory.getInstance().newEditable(NumberFormat.getCurrencyInstance().format(apartment?.monthTax!!.toDouble()))

                        taxaApartamento = apartment.monthTax.toDouble()
                        taxaAtual = taxaTotal - taxaApartamento
                        mDate.text = Editable.Factory.getInstance().newEditable(apartment.vencimento)
                        data = apartment.vencimento
                        if(apartment?.email == ""){
                            mSituation.text = Editable.Factory.getInstance().newEditable("")
                            mMessage.isVisible = false
                        }
                        else {
                            if (apartment!!.regular) {
                                mSituation.text =
                                    Editable.Factory.getInstance().newEditable("Regular")
                                mSituation.setTextColor(Color.GREEN)
                            } else {
                                mSituation.text =
                                    Editable.Factory.getInstance().newEditable("Indadimplente")
                                mSituation.setTextColor(Color.RED)
                            }
                        }
                        mFloor.text = Editable.Factory.getInstance().newEditable(apartment?.floor.toString())
                        andar = apartment?.floor.toString()
                        if(mFirstName.text.toString() == "") {
                            mEdit.text = ("Adicionar inquilino")

                        }
                        else{
                            mEdit.text = ("Editar inquilino")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.detail_button_edit ->{
                val it = Intent(applicationContext,LiverRegistActivity::class.java)
                it.putExtra("FloorNum", andar)
                it.putExtra("ApId", mApartId)
                it.putExtra("data", data)

                startActivity(it)

            }
            R.id.detail_button_message ->{

                val url = "https://api.whatsapp.com/send?phone=55${mTelefone.text}&text=Olá,tudo bem?"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            R.id.detail_button_pay ->{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, 1)
                }
            }




        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 ->{
                if(resultCode == RESULT_OK){
                    mDate.text = Editable.Factory.getInstance().newEditable("Pago")
                    mTax.text = Editable.Factory.getInstance().newEditable(NumberFormat.getCurrencyInstance().format(0.0))
                    FirebaseDatabase.getInstance().getReference("/users/${mAuth.uid}/Condominio/andares/${mApartId}/vencimento").setValue("Pago")
                    FirebaseDatabase.getInstance().getReference("/users/${mAuth.uid}/Condominio/tax").setValue(taxaAtual)
                    FirebaseDatabase.getInstance().getReference("/users/${mAuth.uid}/Condominio/andares/${mApartId}/monthTax").setValue("0.0")
                    FirebaseDatabase.getInstance().getReference("/users/${mAuth.uid}/Condominio/andares/${mApartId}/regular").setValue(true)
                }
            }
        }
    }

}