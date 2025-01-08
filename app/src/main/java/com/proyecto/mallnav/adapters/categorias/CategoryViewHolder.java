package com.proyecto.mallnav.adapters.categorias;

import static com.proyecto.mallnav.utils.Constants.KEY_CATEGORY_NAME;
import static com.proyecto.mallnav.utils.Constants.KEY_CATEGORY_ID;
import static com.proyecto.mallnav.utils.Constants.CATEGORY_SELECTED;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.mallnav.R;
import com.proyecto.mallnav.models.Categoria;


public class CategoryViewHolder extends RecyclerView.ViewHolder{
    protected TextView categoryName = null;
    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.li_category_name);
    }

    public void bind(Categoria categoria) {
        categoryName.setText(categoria.getNombre());
        itemView.setOnClickListener(view -> {
            Intent i = new Intent(CATEGORY_SELECTED);
            i.putExtra(KEY_CATEGORY_NAME, categoria.getNombre());
            i.putExtra(KEY_CATEGORY_ID, categoria.getId());
            view.getContext().sendBroadcast(i);
        });
    }
}
