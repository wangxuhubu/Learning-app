package com.learning_app.user.chathamkulam.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning_app.user.chathamkulam.Adapters.DashMainListAdapter;
import com.learning_app.user.chathamkulam.SearchFilters.DashCardFilterAdapter;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashEntityObjects;
import com.learning_app.user.chathamkulam.Model.DashboardModel.DashSubjectEntity;
import com.learning_app.user.chathamkulam.R;
import com.learning_app.user.chathamkulam.Sqlite.StoreEntireDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FMDashboard extends Fragment {

    //    MainList Items
    ListView dashListView;
    DashMainListAdapter dashMainListAdapter;

    ArrayList<DashEntityObjects> mainList;
    JSONArray mainArray;

    View view;

    RecyclerView dashFilterView;
    DashCardFilterAdapter filterAdapter;

    public FMDashboard() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fm_dash, container, false);

        dashListView = (ListView) view.findViewById(R.id.dashMainListView);
        dashFilterView = (RecyclerView)view.findViewById(R.id.dashFilterView);
        dashFilterView.setVisibility(View.GONE);

        mainList = new ArrayList<DashEntityObjects>();
        mainArray = new JSONArray();

        final MyAsyncTask myAsyncTask = new MyAsyncTask(mainArray,mainList,getActivity());
        myAsyncTask.execute();

        StoreEntireDetails storeEntireDetails = new StoreEntireDetails(getActivity());
        Cursor cursor = storeEntireDetails.getAllDetails();
        Log.d("dashCursorCount", String.valueOf(cursor.getCount()));
        cursor.close();

        Log.d("packageName",getActivity().getPackageName());

//        if (cursor.getCount() != 0){
//
//            if (cursor.moveToFirst()){
//
//                do {
//
//                    Log.d("#country",cursor.getString(1)+"  ");
//                    Log.d("#university", cursor.getString(2)+"  ");
//                    Log.d("#course", cursor.getString(3)+"  ");
//                    Log.d("#sem", cursor.getString(4)+"  ");
//                    Log.d("#subject", cursor.getString(5)+"  ");
//                    subject = cursor.getString(5);
//                    Log.d("#sub_no", cursor.getString(6)+"  ");
//                    Log.d("#subject_id", cursor.getString(7)+"  ");
//                    Log.d("#free_validity", cursor.getString(8)+"  ");
//                    freeValidity = cursor.getString(8);
//                    Log.d("#paid_validity", cursor.getString(9)+"  ");
//                    Log.d("#duration", cursor.getString(10)+"  ");
//
//                }while (cursor.moveToNext());
//            }
//        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Dashboard");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.menu_share).setVisible(true);
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.menu_submit).setVisible(false);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search : {

                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                dashListView.setVisibility(View.GONE);
                dashFilterView.setVisibility(View.VISIBLE);

                MyAsyncTaskFilter myAsyncTaskFilter = new MyAsyncTaskFilter(mainArray,mainList,getActivity());
                myAsyncTaskFilter.execute();
                item.expandActionView();

                MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {

//                        Toast.makeText(getActivity(),"clicked",Toast.LENGTH_LONG).show();
//                        Write your code here
                        dashFilterView.setVisibility(View.GONE);
                        dashListView.setVisibility(View.VISIBLE);

                        return true;
                    }
                });

                search(searchView);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        JSONArray mainArray;
        ArrayList<DashEntityObjects> mainList;
        ProgressDialog progressDialog;
        Context context;

        MyAsyncTask(JSONArray mainArray, ArrayList<DashEntityObjects> mainList, Context context) {
            this.mainArray = mainArray;
            this.mainList = mainList;
            this.progressDialog = new ProgressDialog(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread.
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setTitle("Fetching Data");
            this.progressDialog.setMessage("Please give a movement while we process your data...");
            this.progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

//            this method will be running on background thread so don't update UI frome here

//            Get Values From DataBase
            StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
            Cursor mainCursor = storeEntireDetails.groupMainDetails();

//        Create Main Array
            mainArray = new JSONArray();
            if (mainCursor.getCount() != 0){

                if (mainCursor.moveToFirst()){

                    do {
                        try {
//                        Create Main Object
                            JSONObject mainJsonObject = new JSONObject();

//                        Create Sub Array
                            JSONArray subArray = new JSONArray();

                            mainJsonObject.put("country",mainCursor.getString(1));
                            mainJsonObject.put("university", mainCursor.getString(2));
                            mainJsonObject.put("course",mainCursor.getString(3));
                            mainJsonObject.put("semester",mainCursor.getString(4));

                            Cursor cursor = storeEntireDetails.groupSubDetails(mainCursor.getString(6),mainCursor.getString(4));

                            if (cursor.getCount() != 0){

                                if (cursor.moveToFirst()){

                                    do {

//                                    Create Sub Object
                                        JSONObject subJsonObject = new JSONObject();
                                        subJsonObject.put("subject_name",cursor.getString(5));
                                        subJsonObject.put("subject_id",cursor.getString(6));
                                        subJsonObject.put("subject_no",cursor.getString(7));
                                        subJsonObject.put("free_validity",cursor.getString(8));
                                        subJsonObject.put("paid_validity",cursor.getString(9));
                                        subJsonObject.put("duration",cursor.getString(10));
                                        subJsonObject.put("video_count",cursor.getString(11));
                                        subJsonObject.put("notes_count",cursor.getString(12));
                                        subJsonObject.put("qbank_count",cursor.getString(13));

//                                    Add SubObject To SubArray
                                        subArray.put(subJsonObject);
                                    }while (cursor.moveToNext());
                                }
                            }
//                        Add SubArray To MainObject
                            mainJsonObject.put("subject_details",subArray);

                            mainArray.put(mainJsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }while(mainCursor.moveToNext());

                    Log.d("jsonValue", String.valueOf(mainArray));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            this method will be running on UI thread
            if (progressDialog.isShowing()){
                this.progressDialog.dismiss();

//                ListOut the DataBase Values
                ObjectMapper mapper = new ObjectMapper();
                List<DashEntityObjects> dashEntityObjects = new ArrayList<>();

                List<DashSubjectEntity> subList = new ArrayList<DashSubjectEntity>();

                try {
                    dashEntityObjects =  mapper.readValue(String.valueOf(mainArray), new TypeReference<List<DashEntityObjects>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }

                DashEntityObjects objects = new DashEntityObjects();
                DashSubjectEntity dashSubjectEntity = new DashSubjectEntity();

                for (DashEntityObjects entityObject : dashEntityObjects){

                    entityObject.setCountry(entityObject.getCountry());
                    entityObject.setUniversity(entityObject.getUniversity());
                    entityObject.setCourse(entityObject.getCourse());
                    entityObject.setSemester(entityObject.getSemester());

                    Log.d("CountryName: ", entityObject.getCountry());
                    Log.d("University: ", entityObject.getUniversity());
                    Log.d("Course: ", entityObject.getCourse());
                    Log.d("Semester: ",entityObject.getSemester());

                    for(int i=0;i<entityObject.getSubject_details().size();i++){

                        dashSubjectEntity.setSubject_name(entityObject.getSubject_details().get(i).getSubject_name());
                        dashSubjectEntity.setSubject_id(entityObject.getSubject_details().get(i).getSubject_id());
                        dashSubjectEntity.setSubject_no(entityObject.getSubject_details().get(i).getSubject_no());
                        dashSubjectEntity.setFree_validity(entityObject.getSubject_details().get(i).getFree_validity());
                        dashSubjectEntity.setPaid_validity(entityObject.getSubject_details().get(i).getPaid_validity());
                        dashSubjectEntity.setDuration(entityObject.getSubject_details().get(i).getDuration());
                        dashSubjectEntity.setVideo_count(entityObject.getSubject_details().get(i).getVideo_count());
                        dashSubjectEntity.setNotes_count(entityObject.getSubject_details().get(i).getNotes_count());
                        dashSubjectEntity.setQbank_count(entityObject.getSubject_details().get(i).getQbank_count());

                        Log.d("subjectName: ", entityObject.getSubject_details().get(i).getSubject_name());
                        Log.d("Duration: ", entityObject.getSubject_details().get(i).getDuration());
                        subList.add(dashSubjectEntity);
                    }

                    objects.setSubject_details(subList);

                    mainList.add(entityObject);
                }

//                Initialize views

                if (getActivity()!=null){

                    dashMainListAdapter = new DashMainListAdapter(getActivity(),mainList);
                    dashListView.setAdapter(dashMainListAdapter);
                    dashMainListAdapter.notifyDataSetChanged();
                }

            }
        }
    }


    public class MyAsyncTaskFilter extends AsyncTask<Void, Void, Void> {

        JSONArray mainArray;
        ArrayList<DashEntityObjects> mainList;
        ProgressDialog progressDialog;
        Context context;

        public MyAsyncTaskFilter(JSONArray mainArray, ArrayList<DashEntityObjects> mainList,Context context) {
            this.mainArray = mainArray;
            this.mainList = mainList;
            this.progressDialog = new ProgressDialog(context);
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread.
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setTitle("Fetching Data");
            this.progressDialog.setMessage("Please give a movement while we process your data...");
            this.progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

//            this method will be running on background thread so don't update UI frome here

//            Get Values From DataBase
            StoreEntireDetails storeEntireDetails = new StoreEntireDetails(context);
            Cursor mainCursor = storeEntireDetails.getAllDetails();

            try {

//        Create Main Array
                mainArray = new JSONArray();
                if (mainCursor.getCount() != 0){

                    if (mainCursor.moveToFirst()){

                        do {
//                        Create Main Object
                            JSONObject mainJsonObject = new JSONObject();

                            mainJsonObject.put("country",mainCursor.getString(1));
                            mainJsonObject.put("university", mainCursor.getString(2));
                            mainJsonObject.put("course",mainCursor.getString(3));
                            mainJsonObject.put("semester",mainCursor.getString(4));
                            mainJsonObject.put("subject_name",mainCursor.getString(5));
                            mainJsonObject.put("subject_id",mainCursor.getString(6));
                            mainJsonObject.put("subject_no",mainCursor.getString(7));
                            mainJsonObject.put("free_validity",mainCursor.getString(8));
                            mainJsonObject.put("paid_validity",mainCursor.getString(9));
                            mainJsonObject.put("duration",mainCursor.getString(10));
                            mainJsonObject.put("video_count",mainCursor.getString(11));
                            mainJsonObject.put("notes_count",mainCursor.getString(12));
                            mainJsonObject.put("qbank_count",mainCursor.getString(13));

                            mainArray.put(mainJsonObject);

                        }while (mainCursor.moveToNext());

                    }

                    Log.d("jsonValue", String.valueOf(mainArray));
                }
                mainCursor.close();
            }catch (JSONException e){

                Log.d("JSONException", String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

//            this method will be running on UI thread
            if (progressDialog.isShowing()){
                this.progressDialog.dismiss();

//                ListOut the DataBase Values
                ObjectMapper mapper = new ObjectMapper();
                List<DashEntityObjects> dashEntityObjects = new ArrayList<>();

                ArrayList<DashEntityObjects> mainList = new ArrayList<DashEntityObjects>();

                try {
                    dashEntityObjects =  mapper.readValue(String.valueOf(mainArray), new TypeReference<List<DashEntityObjects>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (DashEntityObjects entityObject : dashEntityObjects){

                    entityObject.setCountry(entityObject.getCountry());
                    entityObject.setUniversity(entityObject.getUniversity());
                    entityObject.setCourse(entityObject.getCourse());
                    entityObject.setSemester(entityObject.getSemester());
                    entityObject.setSubject_name(entityObject.getSubject_name());
                    entityObject.setSubject_id(entityObject.getSubject_id());
                    entityObject.setSubject_no(entityObject.getSubject_no());
                    entityObject.setFree_validity(entityObject.getFree_validity());
                    entityObject.setPaid_validity(entityObject.getPaid_validity());
                    entityObject.setDuration(entityObject.getDuration());
                    entityObject.setVideo_count(entityObject.getVideo_count());
                    entityObject.setNotes_count(entityObject.getNotes_count());
                    entityObject.setQbank_count(entityObject.getQbank_count());

                    Log.d("CountryName: ", entityObject.getCountry());
                    Log.d("University: ", entityObject.getUniversity());
                    Log.d("Course: ", entityObject.getCourse());
                    Log.d("Semester: ",entityObject.getSemester());
                    Log.d("subjectName: ", entityObject.getSubject_name());
                    Log.d("Duration: ", entityObject.getDuration());

                    mainList.add(entityObject);
                }

//                Initialize views

                if (getActivity()!= null){

                    filterAdapter = new DashCardFilterAdapter(getActivity(),mainList);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    dashFilterView = (RecyclerView)view.findViewById(R.id.dashFilterView);
                    dashFilterView.setLayoutManager(mLayoutManager);
                    dashFilterView.setHasFixedSize(true);
                    dashFilterView.setAdapter(filterAdapter);
                    filterAdapter.notifyDataSetChanged();

                }
            }
        }
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
