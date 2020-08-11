package marketlist.rarl.cursos.menudecomidasbasic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.alain.cursos.menudecomidasbasic.R;

import marketlist.rarl.cursos.menudecomidasbasic.dummy.DummyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComidaListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String PATH_FOOD = "food";
    private static final String PATH_PROFILE = "profile";
    private static final String PATH_CODE = "code";

    @BindView(R.id.edtNombre)
    EditText edtNombre;
    @BindView(R.id.edtPrecio)
    EditText edtPrecio;
    @BindView(R.id.btnGuardar)
    Button btnGuardar;
    @BindView(R.id.spinnerFood)
    Spinner spinnerFood;
    @BindView(R.id.btnActualizarSpinner)
    Button btnActualizarSpinner;

    List<DummyContent.Comida> comidaList;
    DummyContent.Comida comidaUpdate;
    ArrayAdapter<String> aaComidas;


    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.comida_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        configSpinner();

        View recyclerView = findViewById(R.id.comida_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void configSpinner() {
        spinnerFood.setOnItemSelectedListener(this);

        aaComidas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        aaComidas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFood.setAdapter(aaComidas);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        comidaUpdate = comidaList.get(position);
        edtNombre.setText(comidaUpdate.getNombre());
        edtPrecio.setText(comidaUpdate.getPrecio());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(PATH_FOOD);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DummyContent.Comida comida = snapshot.getValue(DummyContent.Comida.class);
                comida.setId(snapshot.getKey());
                //Se verifica que no contenga el objeto ya, en caso de no ser así, se agrega
                if (!DummyContent.ITEMS.contains(comida)) {
                    DummyContent.addItem(comida);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DummyContent.Comida comida = snapshot.getValue(DummyContent.Comida.class);
                comida.setId(snapshot.getKey());
                //Se verifica que el objeto exista, y tras ello se actualiza
                if (DummyContent.ITEMS.contains(comida)) {
                    DummyContent.updateItem(comida);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                DummyContent.Comida comida = snapshot.getValue(DummyContent.Comida.class);
                comida.setId(snapshot.getKey());
                //Se verifica que el objeto exista, y tras ello se elimina
                if (DummyContent.ITEMS.contains(comida)) {
                    DummyContent.deleteItem(comida);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Toast.makeText(ComidaListActivity.this, "Object moved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ComidaListActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btnGuardar)
    public void onViewClicked() {
        DummyContent.Comida comida = new DummyContent.Comida(edtNombre.getText().toString().trim(),
                edtPrecio.getText().toString().trim());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(PATH_FOOD);

        //DummyContent.Comida comidaUpdate = DummyContent.getComida(comida.getNombre());
        if (comidaUpdate != null) {
            reference.child(comidaUpdate.getId()).setValue(comida).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        comidaUpdate = null;
                }
            });
        } else {
            reference.push().setValue(comida);
        }
        edtNombre.setText("");
        edtPrecio.setText("");
        Toast.makeText(this, "Data has been uploaded", Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_info:
                final TextView txtCodigo = new TextView(this);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                txtCodigo.setLayoutParams(params);
                txtCodigo.setGravity(Gravity.CENTER_HORIZONTAL);
                txtCodigo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference(PATH_PROFILE).child(PATH_CODE);

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txtCodigo.setText(snapshot.getValue(String.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ComidaListActivity.this, "Cannot load code", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.comida_list_dialog_title)
                        .setPositiveButton(R.string.comida_list_dialog_title_ok, null);
                builder.setView(txtCodigo);
                builder.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btnActualizarSpinner)
    public void onRefreshSpinnerClicked() {
        comidaList = new ArrayList<>();
        aaComidas.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(PATH_FOOD);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    DummyContent.Comida comida = snapshot1.getValue(DummyContent.Comida.class);
                    comida.setId(snapshot1.getKey());
                    comidaList.add(comida);
                    aaComidas.add(comida.getNombre());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ComidaListActivity.this, "Error when consulting",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ComidaListActivity mParentActivity;
        private final List<DummyContent.Comida> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.Comida item = (DummyContent.Comida) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ComidaDetailFragment.ARG_ITEM_ID, item.getId());
                    ComidaDetailFragment fragment = new ComidaDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.comida_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ComidaDetailActivity.class);
                    intent.putExtra(ComidaDetailFragment.ARG_ITEM_ID, item.getId());

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ComidaListActivity parent,
                                      List<DummyContent.Comida> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comida_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText("S/. " + mValues.get(position).getPrecio());
            holder.mContentView.setText(mValues.get(position).getNombre());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference(PATH_FOOD);
                    reference.child(mValues.get(position).getId()).removeValue();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @OnClick(R.id.btnBorrar)
        public void onViewClicked() {
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            @BindView(R.id.btnBorrar)
            Button btnBorrar;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.nombre);
            }
        }
    }
}
