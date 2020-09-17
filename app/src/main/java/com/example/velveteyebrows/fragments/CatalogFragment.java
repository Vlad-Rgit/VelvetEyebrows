package com.example.velveteyebrows.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cf.feuerkrieg.web_api_helper.Requester;
import com.example.velveteyebrows.AppData;
import com.example.velveteyebrows.R;
import com.example.velveteyebrows.activities.MainActivity;
import com.example.velveteyebrows.adapters.ServiceRecycleAdapter;
import com.example.velveteyebrows.animations.BackdropRevealAnimator;
import com.example.velveteyebrows.callbacks.FilterServicesCallback;
import com.example.velveteyebrows.comparators.ServiceAlphComparator;
import com.example.velveteyebrows.comparators.ServicePriceComparator;
import com.example.velveteyebrows.database.DbAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.velveteyebrows.dto.ServiceDTO;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;


public class CatalogFragment extends Fragment {

    public static final String ALL_SERVICES_STATE = "AllServicesState";
    public static final String LIST_SERVICES_STAET = "ListServicesState";
    public static final String SCROLL_POSITION_STATE = "ScrollPositionState";

    private TextView _tvCatalogHeader;
    private BackdropRevealAnimator _backdropRevealAnimator;

    private RecyclerView _rvDiscountServices;
    private List<ServiceDTO> _allServices;
    private ServiceRecycleAdapter _discountServicesAdapter;
    private SwipeRefreshLayout _swpCatalog;
    private SearchView _searchView;
    private TextView _tvNoSearchResults;
    private FilterServicesCallback _filterServicesCallback;
    private MainActivity _mainActivity;
    private CatalogBackLayerFragment _backLayerFragment;
    private ServiceAlphComparator _serviceFavComparator;


    public CatalogFragment() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        _mainActivity = (MainActivity)context;

        _backdropRevealAnimator = new BackdropRevealAnimator(
                _mainActivity, _mainActivity.findViewById(R.id.front_layer)
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        _serviceFavComparator = new ServiceAlphComparator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_catalog, container, false);


        //Init recycle view
        _rvDiscountServices = view.findViewById(R.id.rv_discountServices);

        _rvDiscountServices.setHasFixedSize(true);

        _rvDiscountServices.setLayoutManager(new LinearLayoutManager(getContext()));

        _rvDiscountServices.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = 10;
                outRect.top = 10;
                outRect.left = 10;
                outRect.right = 10;
            }
        });

        //Init swype refresh layout
        _swpCatalog = view.findViewById(R.id.swp_catalog);

        _swpCatalog.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetServicesTask().execute();
            }
        });


        //Init some text views
        _tvNoSearchResults = view.findViewById(R.id.tv_no_search_results);

        _tvCatalogHeader = view.findViewById(R.id.tv_catalog_header);

        _tvCatalogHeader.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(_backdropRevealAnimator.isBackdropShown()) {
                    changeBacklayerState();
                }

                return false;
            }
        });


        //Create instance
        if(savedInstanceState == null) {
            new GetServicesTask().execute();
        }
        //Restore instance
        else{

            _allServices = (List<ServiceDTO>)
                    savedInstanceState.getSerializable(ALL_SERVICES_STATE);

            List<ServiceDTO> listState = (List<ServiceDTO>)
                    savedInstanceState.getSerializable(LIST_SERVICES_STAET);

            int scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_STATE);

            _discountServicesAdapter = new ServiceRecycleAdapter(
                    _mainActivity, R.layout.service_list_item, listState, _serviceFavComparator
            );

            _discountServicesAdapter.setOnSignUpCallback(this::onSignUp);

            _rvDiscountServices.setAdapter(_discountServicesAdapter);
            _rvDiscountServices.scrollToPosition(scrollPosition);

            initFilterServiceCallback(_allServices, listState, _rvDiscountServices, _discountServicesAdapter);

            if(_mainActivity.getBackLayerFragment() instanceof CatalogBackLayerFragment){
                _backLayerFragment = (CatalogBackLayerFragment)_mainActivity.getBackLayerFragment();
                _backLayerFragment.setFilterChangedCallback(_filterServicesCallback);
            }
            else{
                initBacklayer();
            }

            _filterServicesCallback.applyFilterToAdapter();
        }

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.catalog_menu, menu);


      /*  _searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                _searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                _filterState.query = newText;

                Collection<ServiceDTO> filtered = filter(_allServices, _filterState);

                if(filtered.isEmpty()) {
                    _discountServicesAdapter.clear();
                    _swpCatalog.setVisibility(View.GONE);
                    _tvNoSearchResults.setVisibility(View.VISIBLE);
                }
                else{
                    _discountServicesAdapter.replaceAll(filtered);
                    _rvDiscountServices.scrollToPosition(0);
                    _tvNoSearchResults.setVisibility(View.GONE);
                    _swpCatalog.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });*/

        MenuItem filterItem = menu.findItem(R.id.navigation_filter);

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                changeBacklayerState();

                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void changeBacklayerState() {
        if (_backLayerFragment != null) {

            _backdropRevealAnimator.animate();

            if(_backdropRevealAnimator.isBackdropShown()) {

                int amount = _discountServicesAdapter.getItemCount();

                String amountText = getContext().getResources().getQuantityString(
                        R.plurals.services, amount, amount
                );

                _tvCatalogHeader.setText(amountText);
            }
            else{
                _filterServicesCallback.applyFilterToAdapter();
                _tvCatalogHeader.setText("Catalog");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putSerializable(ALL_SERVICES_STATE, (Serializable) _allServices);

        if(_discountServicesAdapter != null) {
            outState.putSerializable(LIST_SERVICES_STAET, (Serializable) _discountServicesAdapter.getItems());
        }

        if(_rvDiscountServices != null) {
            outState.putInt(SCROLL_POSITION_STATE, _rvDiscountServices.getVerticalScrollbarPosition());
        }
    }


    private void initCollections(List<ServiceDTO> services){

        _allServices = services;

        _discountServicesAdapter =
                new ServiceRecycleAdapter(getContext(),
                        R.layout.service_list_item,
                        _allServices,
                        _serviceFavComparator);

        _discountServicesAdapter.setOnSignUpCallback(this::onSignUp);

        _rvDiscountServices.setAdapter(_discountServicesAdapter);
    }

    private void initFilterServiceCallback(List<ServiceDTO> allServices,
                                           RecyclerView recyclerView,
                                           ServiceRecycleAdapter adapter){
        _filterServicesCallback = new FilterServicesCallback(allServices, recyclerView, adapter);

        bindCallbacksToFilteredService();
    }

    private void initFilterServiceCallback(List<ServiceDTO> allServices,
                                           Collection<ServiceDTO> filterdService,
                                           RecyclerView recyclerView,
                                           ServiceRecycleAdapter adapter){
        _filterServicesCallback = new FilterServicesCallback(allServices, filterdService, recyclerView, adapter);

        bindCallbacksToFilteredService();
    }

    private void bindCallbacksToFilteredService(){
        _filterServicesCallback.setOnFilterCompleteCallback(new FilterServicesCallback.OnFilterCompleteCallback() {
            @Override
            public void filterComplete(int elements) {

                if(_backdropRevealAnimator.isBackdropShown()) {
                    String amountText = getResources().getQuantityString(R.plurals.services, elements, elements);

                    _tvCatalogHeader.setText(amountText);
                }

                if(elements>0){
                    _swpCatalog.setVisibility(View.VISIBLE);
                    _tvNoSearchResults.setVisibility(View.GONE);
                }
                else{
                    _swpCatalog.setVisibility(View.GONE);
                    _tvNoSearchResults.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void initBacklayer(){

        ServicePriceComparator comparator = new ServicePriceComparator();

        float minPrice = Collections.min(_allServices, comparator)
                .getFinalCost();

        float maxPrice = Collections.max(_allServices, comparator)
                .getFinalCost();

        _backLayerFragment = CatalogBackLayerFragment.newInstance(minPrice, maxPrice);

        _backLayerFragment.setFilterChangedCallback(_filterServicesCallback);

        _mainActivity.setBacklayer(_backLayerFragment);
    }

    private void onSignUp(ServiceDTO serviceDTO){
        _mainActivity.goToSignUp(serviceDTO);
    }

    public class GetServicesTask extends AsyncTask<Void, Void, List<ServiceDTO>> {

        @Override
        protected List<ServiceDTO> doInBackground(Void... voids) {


            String url = AppData.API_ADDRESS_SLASH + "services";

            String jsonAllServices = Requester.makeJsonGetRequest(url);

            try(DbAdapter dbAdapter = new DbAdapter(CatalogFragment.this.getContext())) {

                if (jsonAllServices == null) {

                    AppData.isLocal = true;
                    return dbAdapter.getServices();

                } else {

                    Gson gson = new Gson();

                    Type type = new TypeToken<List<ServiceDTO>>() {
                    }.getType();

                    List<ServiceDTO> services = gson.fromJson(jsonAllServices, type);
                    dbAdapter.addServices(services);
                    return services;
                }
            }
        }

        @Override
        protected void onPostExecute(List<ServiceDTO> list) {

            //If we it is the first call of GetServices we init collections, back layer and filter state changed callback
            if(_discountServicesAdapter == null
             || _rvDiscountServices.getAdapter() == null) {

                initCollections(list);

                initFilterServiceCallback(
                        _allServices, _rvDiscountServices, _discountServicesAdapter
                );

                initBacklayer();
            }
            //If we it is not the first call we just apply filter for new all services collection and refresh list
            else{
                _allServices = list;
                _filterServicesCallback.setAllServices(_allServices);
                _filterServicesCallback.filter(_backLayerFragment.getFilterState());
                _filterServicesCallback.applyFilterToAdapter();
            }

            _swpCatalog.setRefreshing(false);
        }
    }
}
