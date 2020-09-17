package com.example.velveteyebrows.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import cf.feuerkrieg.web_api_helper.Requester;
import com.android.volley.toolbox.ImageLoader;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.R;
import com.example.velveteyebrows.database.DbAdapter;
import com.example.velveteyebrows.network.AnimatedNetworkImageView;
import com.example.velveteyebrows.network.ServiceImageRequester;
import com.velveteyebrows.dto.ServiceDTO;

import java.security.Provider;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class ServiceRecycleAdapter
        extends RecyclerView.Adapter<ServiceRecycleAdapter.ViewHolder> {


    public interface OnSignUpCallback {
        void onSignUp(ServiceDTO serviceDTO);
    }

    private OnSignUpCallback _onSignUpCallback;
    private final SortedList<ServiceDTO> _services;
    private final int _resourceId;
    private final LayoutInflater _inflater;
    private final Context _context;
    private static final DecimalFormat _decimalFormat;


    static {

        //Create object to format decimal numbers
        _decimalFormat = new DecimalFormat();
        _decimalFormat.setGroupingUsed(false);
        _decimalFormat.setMinimumFractionDigits(0);
        _decimalFormat.setMaximumFractionDigits(2);
    }

    public ServiceRecycleAdapter(Context context, int resourceId,
                                 List<ServiceDTO> services, Comparator<ServiceDTO> comparator){


        _services = new SortedList<>(ServiceDTO.class,
                new ServiceSortedListCallback(comparator, this));

        _services.addAll(services);

        _resourceId = resourceId;
        _context = context;

        _inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = _inflater.inflate(_resourceId, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final ServiceDTO service = _services.get(position);

        boolean isFav = false;

        if(AppData.isLocal) {
            isFav = service.getFavourited();
        }
        else {
            //Check if service is favourite
            try (DbAdapter dbAdapter = new DbAdapter(_context)) {

                if (dbAdapter.isServiceExists(service)) {
                    isFav = dbAdapter.isServiceFav(service);
                } else {
                    dbAdapter.addService(service);
                }
            }
        }

        //Set fav icon
        Drawable ibtnSrc = isFav ?
                _context.getDrawable(R.drawable.baseline_favorite_black_24)
                : _context.getDrawable(R.drawable.baseline_favorite_border_black_24);

        final AppCompatImageButton ibtn = holder.getIbtnFav();

        ibtn.setImageDrawable(ibtnSrc);

        boolean finalIsFav = isFav;

        ibtn.setOnClickListener(new View.OnClickListener() {

            private boolean _isFav = finalIsFav;

            @Override
            public void onClick(View v) {

                _isFav = !_isFav;

                try(DbAdapter dbAdapter = new DbAdapter(_context)){

                    dbAdapter.setServiceFav(service, _isFav);

                    Drawable ibtnSrc = _isFav ?
                            _context.getDrawable(R.drawable.baseline_favorite_black_24)
                            : _context.getDrawable(R.drawable.baseline_favorite_border_black_24);

                    ibtn.setImageDrawable(ibtnSrc);
                }
            }
        });



        //Set general info

        if(service.getDescription() != null){
            holder.getTvServiceDesc().setVisibility(View.VISIBLE);
            holder.getTvServiceDesc().setText(service.getDescription());
        }else{
            holder.getTvServiceDesc().setVisibility(View.GONE);
        }

        holder.getTvServiceName().setText(service.getTitle());

        holder.getTvCost().setText(_decimalFormat.format(service.getCost())+"₽");

        //Set duration

        String duration = service.getDuration() + " min.";

        holder.getTvDuration().setText(duration);

        float discount = service.getDiscount();

        //Set possible discount
        if(discount != 0) {

            if((holder.getTvCost().getPaintFlags()
                    & Paint.STRIKE_THRU_TEXT_FLAG) != Paint.STRIKE_THRU_TEXT_FLAG) {

                holder.getTvCost().setPaintFlags(holder.getTvCost().getPaintFlags() ^
                        Paint.STRIKE_THRU_TEXT_FLAG);
            }


            float cost = service.getCost().floatValue();
            float newCost = cost - cost*discount;

            String discountText = String.format("Discount %d %%", (int)(discount*100));

            holder.getTvDiscount().setVisibility(View.VISIBLE);
            holder.getTvNewCost().setVisibility(View.VISIBLE);

            holder.getTvDiscount().setText(discountText);

            holder.getTvNewCost().setPaintFlags(Paint.UNDERLINE_TEXT_FLAG |
                    holder.getTvNewCost().getPaintFlags());

            holder.getTvNewCost().setText(_decimalFormat.format(newCost)+"₽");
        }
        else{
            holder.getTvDiscount().setVisibility(View.GONE);
            holder.getTvNewCost().setVisibility(View.GONE);

            if((holder.getTvCost().getPaintFlags()
                    & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG){

                holder.getTvCost().setPaintFlags(holder.getTvCost().getPaintFlags() ^
                        Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        //Set image


        AnimatedNetworkImageView nimgMainServiceImage = holder.getNimgServiceMainImage();
        nimgMainServiceImage.setService(service);

        if(service.getImage() == null && !AppData.isLocal) {

            String url = AppData.API_ADDRESS_SLASH
                    + "services/image/"+ service.getId();

            ImageLoader imageLoader = ServiceImageRequester.getInstance(_context)
                    .getImageLoader();

            nimgMainServiceImage.setImageUrl(url, imageLoader);
        }
        else if (service.getImage() != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(service.getImage(),
                    0,
                    service.getImage().length);

            nimgMainServiceImage.setImageBitmapLocal(bmp);
        }

        //Set on sign up callback
        holder._btnRequestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onSignUpCallback.onSignUp(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _services.size();
    }


    public void addAll(Collection<ServiceDTO> services){
        _services.addAll(services);
    }

    public void add(ServiceDTO service){
        _services.add(service);
    }

    public void remove(ServiceDTO service){
        _services.remove(service);
    }

    public void removeAll(Iterable<ServiceDTO> services){
        _services.beginBatchedUpdates();

        for(ServiceDTO service : services){
            _services.remove(service);
        }

        _services.endBatchedUpdates();
    }

    public void replaceAll(Collection<ServiceDTO> services){

        _services.beginBatchedUpdates();

        for(int i = _services.size()-1; i>=0; i--){

            ServiceDTO service = _services.get(i);

            if(!services.contains(service)){
                _services.remove(service);
            }
        }

        _services.addAll(services);
        _services.endBatchedUpdates();
    }

    public void clear(){
        _services.clear();
    }

    public List<ServiceDTO> getItems(){

        ArrayList<ServiceDTO> result = new ArrayList<>(_services.size());

        for(int i = 0; i<_services.size(); i++){
            result.add(_services.get(i));
        }

        return result;
    }

    public void setOnSignUpCallback(OnSignUpCallback callback){
        _onSignUpCallback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final AnimatedNetworkImageView _nimgServiceMainImage;
        private final TextView _tvServiceName;
        private final TextView _tvServiceDesc;
        private final AppCompatImageButton _ibtnFav;
        private final TextView _tvCost;
        private final TextView _tvNewCost;
        private final TextView _tvDuration;
        private final TextView _tvDiscount;
        private final Button _btnRequestService;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            _nimgServiceMainImage =
                    itemView.findViewById(R.id.nimg_serviceMainImage);
            _tvServiceName =
                    itemView.findViewById(R.id.tv_serviceName);
            _tvServiceDesc =
                    itemView.findViewById(R.id.tv_descService);

            _ibtnFav =
                    itemView.findViewById(R.id.ibtn_fav);

            _tvCost =
                    itemView.findViewById(R.id.tv_cost);

            _tvNewCost =
                    itemView.findViewById(R.id.tv_newCost);

            _tvDuration =
                    itemView.findViewById(R.id.tv_duration);

            _tvDiscount =
                    itemView.findViewById(R.id.tv_discount);

            _btnRequestService =
                    itemView.findViewById(R.id.btn_requestService);
        }

        public AnimatedNetworkImageView getNimgServiceMainImage() {
            return _nimgServiceMainImage;
        }

        public TextView getTvServiceName() {
            return _tvServiceName;
        }

        public TextView getTvServiceDesc() {
            return _tvServiceDesc;
        }

        public AppCompatImageButton getIbtnFav() {
            return _ibtnFav;
        }

        public TextView getTvCost() {
            return _tvCost;
        }

        public TextView getTvNewCost() {
            return _tvNewCost;
        }

        public TextView getTvDuration() {
            return _tvDuration;
        }

        public TextView getTvDiscount() {
            return _tvDiscount;
        }

        public Button getBtnRequestService() {
            return _btnRequestService;
        }
    }



}
