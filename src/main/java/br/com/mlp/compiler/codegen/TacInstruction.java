package br.com.mlp.compiler.codegen;

public class TacInstruction {
    private final Opcode opcode;
    private final String[] args;

    public TacInstruction(Opcode opcode, String... args) {
        this.opcode = opcode;
        this.args = args;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opcode.name());
        if (args != null && args.length > 0) {
            sb.append(" ");
            for (int i = 0; i < args.length; i++) {
                sb.append(args[i]);
                if (i < args.length - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }
}
