package com.edbrix.enterprise.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edbrix.enterprise.Interfaces.OrganizationListInterface;
import com.edbrix.enterprise.Models.Organizations;
import com.edbrix.enterprise.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Organizations> list;
    private OrganizationListInterface orgListInterface;

    public OrganizationListAdapter(Context context, ArrayList<Organizations> list, OrganizationListInterface orgListInterface) {

        this.context = context;
        this.list = list;
        this.orgListInterface = orgListInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_organization_list, parent, false);

        return new ViewHolder(v, orgListInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.name.setText(list.get(position).getOrganizationName());

        if (list.get(position).getOrganizationImage()!=null && !list.get(position).getOrganizationImage().isEmpty())
            Picasso.with(context)
                    .load(list.get(position).getOrganizationImage())
                    .error(R.drawable.edbrix_logo)
                    .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshList(ArrayList<Organizations> newList)
    {
        list = new ArrayList<>();
        list = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView imageView;

        ViewHolder(View itemView, final OrganizationListInterface orgRecyclerInterface) {
            super(itemView);

            name = itemView.findViewById(R.id.org_name);
            imageView = itemView.findViewById(R.id.org_image);

            itemView.setClickable(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orgRecyclerInterface.onOrgSelected(list.get(getLayoutPosition()).getId(),
                            list.get(getLayoutPosition()).getOrganizationName(), list.get(getLayoutPosition()).getOrganizationImage());
                }
            });
        }
    }

}
