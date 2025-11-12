package br.com.mlp.diagnostics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorReporter {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public void add(Diagnostic d) { diagnostics.add(d); }

    public List<Diagnostic> all() { return Collections.unmodifiableList(diagnostics); }

    public boolean hasErrorsOfType(ErrorType t) {
        return diagnostics.stream().anyMatch(d -> d.getType() == t);
    }

    public boolean hasAnyError() {
        return !diagnostics.isEmpty();
    }
}
