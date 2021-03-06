package com.datamation.hmdsfa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.datamation.hmdsfa.R;
import com.datamation.hmdsfa.controller.ItemController;
import com.datamation.hmdsfa.model.InvDet;
import com.datamation.hmdsfa.model.OrderDetail;

import java.math.BigDecimal;
import java.util.ArrayList;

public class PrintOrderItemAdapter extends ArrayAdapter<OrderDetail> {
    Context context;
    ArrayList<OrderDetail> list;
    String refno;
    BigDecimal disc;

    public PrintOrderItemAdapter(Context context, ArrayList<OrderDetail> list) {

        super(context, R.layout.row_printitems_listview, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {

        LayoutInflater inflater = null;
        View row = null;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_printitems_listview, parent, false);

        TextView variantcode = (TextView) row.findViewById(R.id.printvariantcode);
        TextView articleno = (TextView) row.findViewById(R.id.printarticleno);
        TextView unitprice = (TextView) row.findViewById(R.id.unitprice);
        TextView printindex = (TextView) row.findViewById(R.id.printindexVan);
        TextView disper = (TextView) row.findViewById(R.id.printdisper);
        TextView description = (TextView) row.findViewById(R.id.printdescription);
        TextView qty = (TextView) row.findViewById(R.id.printqty);
        TextView discamt = (TextView) row.findViewById(R.id.printdiscamt);
        TextView amount = (TextView) row.findViewById(R.id.printlineamount);

        description.setText(""+new ItemController(context).getItemNameByCode(list.get(position).getFORDERDET_ITEMCODE()));
        variantcode.setText(""+list.get(position).getFORDERDET_VARIANTCODE());
        articleno.setText(""+list.get(position).getFORDERDET_ARTICLENO());
        unitprice.setText(""+list.get(position).getFORDERDET_SELLPRICE());
        amount.setText(""+list.get(position).getFORDERDET_AMT());
        disper.setText(""+list.get(position).getFORDERDET_DISPER());
        qty.setText(""+list.get(position).getFORDERDET_QTY());
        discamt.setText(""+list.get(position).getFORDERDET_DISAMT());

        position = position + 1;
        String pos = Integer.toString(position);
        printindex.setText(pos + ". ");

        return row;
    }
}
