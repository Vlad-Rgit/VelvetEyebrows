package com.example.velveteyebrows.callbacks;

import androidx.recyclerview.widget.RecyclerView;
import com.example.velveteyebrows.adapters.ServiceRecycleAdapter;
import com.example.velveteyebrows.database.DbAdapter;
import com.example.velveteyebrows.fragments.CatalogBackLayerFragment;
import com.velveteyebrows.dto.ServiceDTO;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class FilterServicesCallback implements
        CatalogBackLayerFragment.OnFilterStateChangedCallback {


    public interface OnFilterCompleteCallback {
        void filterComplete(int elements);
    }

    private OnFilterCompleteCallback _onFilterCompleteCallback;

    private List<ServiceDTO> _allServices;
    private final LinkedList<ServiceDTO> _filteredServices;
    private final RecyclerView _recyclerView;
    private final ServiceRecycleAdapter _adapter;

    public FilterServicesCallback(List<ServiceDTO> allServices,
                                  RecyclerView recyclerView,
                                  ServiceRecycleAdapter adapter){
        _filteredServices = new LinkedList<>();
        _allServices = allServices;
        _recyclerView = recyclerView;
        _adapter = adapter;
    }

    public FilterServicesCallback(List<ServiceDTO> allServices,
                                  Collection<ServiceDTO> filteredServices,
                                  RecyclerView recyclerView,
                                  ServiceRecycleAdapter adapter){
        this(allServices, recyclerView, adapter);
        _filteredServices.addAll(filteredServices);
    }

    @Override
    public void filter(CatalogBackLayerFragment.FilterState filterState) {

        _filteredServices.clear();

        for(ServiceDTO service : _allServices){

            boolean isToAdd = true;

            float finalCost = service.getFinalCost();

            if(filterState.isOnlyFav() && !service.getFavourited()){
                isToAdd = false;
            }
            if(finalCost < filterState.getCurrentMinPrice()){
                isToAdd = false;
            }
            if(finalCost > filterState.getCurrentMaxPrice()){
                isToAdd = false;
            }
            if(service.getDiscount()<filterState.getMinDiscount()){
                isToAdd = false;
            }

            if(isToAdd){
                _filteredServices.add(service);
            }
        }

        if(_onFilterCompleteCallback != null) {
            _onFilterCompleteCallback.filterComplete(_filteredServices.size());
        }
    }

    public void applyFilterToAdapter(){
        if(_filteredServices.size()>0) {
            _adapter.replaceAll(_filteredServices);
            _recyclerView.scrollToPosition(0);
        }
    }


    public void setOnFilterCompleteCallback(OnFilterCompleteCallback callback){
        _onFilterCompleteCallback = callback;
    }

    public void setAllServices(List<ServiceDTO> allServices){
        _allServices = allServices;
    }

    public int getSizeOfFilteredServices(){
        return _filteredServices.size();
    }
}
