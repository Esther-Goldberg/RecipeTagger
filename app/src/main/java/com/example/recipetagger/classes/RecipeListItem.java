package com.example.recipetagger.classes;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipetagger.R;
import com.google.android.material.card.MaterialCardView;

public class RecipeListItem extends RecyclerView.ViewHolder implements View.OnClickListener {


    public final MaterialCardView  docListItem;
    public final ConstraintLayout docListItemLayout;
    public final TextView docName;
    public final Flow tagsFlow;

    public RecipeListItem(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
        docListItem=itemView.findViewById(R.id.document_list_item);
        docListItemLayout=itemView.findViewById(R.id.document_list_item_layout);
        docName=itemView.findViewById(R.id.document_name);
        tagsFlow=itemView.findViewById(R.id.tags_flow);

    }

    @Override
    public void onClick(View v) {
        RecipeListItemAdapter.onItemClickListener.onItemClick(getAdapterPosition(), v);
    }
}
