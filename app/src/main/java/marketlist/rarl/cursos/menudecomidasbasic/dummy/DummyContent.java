package marketlist.rarl.cursos.menudecomidasbasic.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContent {

    public static final List<Comida> ITEMS = new ArrayList<Comida>();


    public static final Map<String, Comida> ITEM_MAP = new HashMap<String, Comida>();

    // set to 0
    private static final int COUNT = 0;

    static {
        // Add some sample items
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    public static void addItem(Comida item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void updateItem(Comida item) {
        ITEMS.set(ITEMS.indexOf(item), item);
        ITEM_MAP.put(item.getId(), item);
    }

    public static void deleteItem(Comida item) {
        ITEMS.remove(item);
        ITEM_MAP.remove(item);
    }

    private static Comida createDummyItem(int position) {
        return new Comida(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of name.
     */
    // Re-create class COMIDA
    public static class Comida {
        private String id;
        private String nombre;
        private String precio;

        public Comida() {
        }

        public Comida(String nombre, String precio) {
            this.nombre = nombre;
            this.precio = precio;
        }

        public Comida(String id, String nombre, String precio) {
            this.id = id;
            this.nombre = nombre;
            this.precio = precio;
        }

        //métodos getter y setter
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getPrecio() {
            return precio;
        }

        public void setPrecio(String precio) {
            this.precio = precio;
        }

        // equals and hasCode unicamente con el ID
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Comida comida = (Comida) o;

            return id != null ? id.equals(comida.id) : comida.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }
}
