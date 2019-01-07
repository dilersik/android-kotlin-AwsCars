package com.dilerdesenvolv.carros.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.extensions.loadUrl
import kotlinx.android.synthetic.main.adapter_carro.view.*

// define o construtor que recebe (carros, onClick)
class CarroAdapter(
        private val carros: MutableList<Carro>,
        private val onClick: (Carro) -> Unit) :
        RecyclerView.Adapter<CarroAdapter.CarrosViewHolder>() {

    // ViewHolder fica vazio pois import do extensios
    class CarrosViewHolder(view: View) : RecyclerView.ViewHolder(view) { }

    // infla o layout do adapter e retorna o ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrosViewHolder {
        // infla a view do adapter
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_carro, parent, false)
        // retorna o ViewHolder que contem todas as views
        return CarrosViewHolder(view)
    }

    // faz o bind para tualizar o valor das views com dados do Carro
    override fun onBindViewHolder(holder: CarrosViewHolder, position: Int) {
        // recupera objeto carro
        val carro = carros[position]
        // atualiza os dados do carro
        with (holder.itemView) {
            tNome.text = carro.nome
            progress.visibility = View.VISIBLE
            // download foto
            img.loadUrl(carro.urlFoto, progress)
            // add o evento de clique na linha
            setOnClickListener { onClick(carro) }
        }
    }

    override fun getItemCount(): Int {
        return this.carros.size
    }

    fun addItemToList(carro: Carro, position: Int) {
        this.carros.add(position, carro)
        notifyItemInserted(position)
    }

}


