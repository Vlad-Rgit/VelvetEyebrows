package com.example.velveteyebrows.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.ImageLoader;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.databinding.AppointmentItemBinding;
import com.example.velveteyebrows.network.ServiceImageRequester;
import com.velveteyebrows.dto.AppointmentDTO;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentsRecycleAdapter
        extends RecyclerView.Adapter<AppointmentsRecycleAdapter.AppointmentViewHolder> {

    private final List<AppointmentDTO> _rows;
    private final LayoutInflater _layoutInflater;
    private final Context _context;

    public AppointmentsRecycleAdapter(Context context, List<AppointmentDTO> rows){
        _rows = rows;
        _context = context;
        _layoutInflater = LayoutInflater.from(_context);
    }


    @NonNull
    @NotNull
    @Override
    public AppointmentsRecycleAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        AppointmentItemBinding itemBinding
                = AppointmentItemBinding.inflate(_layoutInflater, parent, false);
        return new AppointmentViewHolder(_context, itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AppointmentsRecycleAdapter.AppointmentViewHolder holder, int position) {
        holder.bind(_rows.get(position));
    }

    @Override
    public int getItemCount() {
       return  _rows.size();
    }

    @BindingAdapter({"date"})
    public static void convertDateTime(TextView textView, LocalDateTime dateTime){

        String dateText = "Date ";

        SimpleDateFormat formatter = new SimpleDateFormat("h");
        dateText += formatter.format(dateTime);

        textView.setText(dateText);
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder{

        private final AppointmentItemBinding _itemBinding;
        private final Context _context;

        public AppointmentViewHolder(Context context, AppointmentItemBinding itemBinding) {
            super(itemBinding.getRoot());
            _itemBinding = itemBinding;
            _context = context;
        }

        public void bind(AppointmentDTO item){
            _itemBinding.setClientService(item.getClientServiceDTO());
            _itemBinding.setService(item.getServiceDTO());

            String url = AppData.API_ADDRESS_SLASH
                    + "images/service?serviceId="+ item.getServiceDTO().getId();

            ImageLoader imageLoader = ServiceImageRequester.getInstance(_context)
                    .getImageLoader();

            _itemBinding.nimgMainImage.setImageUrl(url, imageLoader);
        }
    }
}
