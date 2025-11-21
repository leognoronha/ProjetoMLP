package br.com.mlp.compiler.semantics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;

import br.com.mlp.compiler.ast.Type;

public class SymbolTable {

    public static class Entry {
        public final String name;
        public final Type type;
        public final int line;
        public final int column;
        public boolean inicializada = false;
        public boolean usada = false;

        public Entry(String name, Type type, int line, int column) {
            this.name = name;
            this.type = type;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("%s : %s (linha %d, col %d)", name, type, line, column);
        }
    }

    private final Map<String, Entry> entries = new LinkedHashMap<>();

    public boolean declare(String name, Type type, int line, int column) {
        if (entries.containsKey(name)) return false;
        entries.put(name, new Entry(name, type, line, column));
        return true;
    }

    public Entry lookup(String name) {
        return entries.get(name);
    }

    public Collection<Entry> all() {
        return entries.values();
    }
}
