package citzen.jbc.myapplication.admin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import citzen.jbc.myapplication.R;
import citzen.jbc.myapplication.design.ThreeTwoImageView;

public class RecentEventsFragment extends Fragment {
    String photoLink, message, dataKey;
    View view;

    @BindView(R.id.recentImage)
    ThreeTwoImageView ivRecent;

    @BindView(R.id.tvRecent)
    TextView tvRecent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recent_act, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setPhotoLink(getArguments().getString("photoLink"));
        setMessage(getArguments().getString("message"));
        setDataKey(getArguments().getString("dataKey"));
    }

    public void setMessage(String message) {
        tvRecent.setText(message);
        Log.e("Message Set", "true");
    }

    public void setPhotoLink(String photoLink) {
        Log.e("Photo Set", "true");
        Picasso.with(getActivity()).load(photoLink).placeholder(R.drawable.placeholder).into(ivRecent);
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    @OnClick(R.id.ibRecentDelete)
    void deleteRecent() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(getString(R.string.recentevent)).child(dataKey);
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(getActivity(),"Event Deletion Successful",Toast.LENGTH_SHORT).show();
                Log.e("Event Deleted", "true");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Event Deletion Error", e.getMessage());
            }
        });
    }
}
