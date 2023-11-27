import java.util.*;

public class ASDI implements Parser {
    private int i = 0;
    Stack<String> pila = new Stack<>();
    public ArrayList<String> terminales = new ArrayList<>();
    public ArrayList<String> nterminales = new ArrayList<>();
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;
    private String[][] tabla = new String[11][9];

    public ASDI(List<Token> tokens) {
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override

    public boolean parse() {
        // terminales
        nterminales.add("Q");
        nterminales.add("D");
        nterminales.add("P");
        nterminales.add("A");
        nterminales.add("A1");
        nterminales.add("A2");
        nterminales.add("A3");
        nterminales.add("T");
        nterminales.add("T1");
        nterminales.add("T2");
        nterminales.add("T3");
        // no terminales
        terminales.add("SELECT");
        terminales.add("DISTINCT");
        terminales.add("FROM");
        terminales.add("ASTERISCO");
        terminales.add("PUNTO");
        terminales.add("COMA");
        terminales.add("IDENTIFICADOR");
        terminales.add("EOF");
        // producciones
        tabla[0][0] = "SELECT D FROM T";
        tabla[1][1] = "DISTINCT P";
        tabla[1][3] = "P";
        tabla[1][6] = "P";
        tabla[2][3] = "ASTERISCO";
        tabla[2][6] = "A";
        tabla[3][6] = "A2 A1";
        tabla[4][2] = "EPSILON";
        tabla[4][5] = "COMA A";
        tabla[5][6] = "IDENTIFICADOR A3";
        tabla[6][2] = "EPSILON";
        tabla[6][4] = "PUNTO IDENTIFICADOR";
        tabla[6][5] = "EPSILON";
        tabla[7][6] = "T2 T1";
        tabla[8][5] = "COMA T";
        tabla[8][7] = "EPSILON";
        tabla[9][6] = "IDENTIFICADOR T3";
        tabla[10][5] = "EPSILON";
        tabla[10][6] = "IDENTIFICADOR";
        tabla[10][7] = "EPSILON";

        // llenando la pila con items iniciales
        pila.push("EOF");
        pila.push("Q");
        // Variables adicionales
        String tope; // tope de la pila
        int index; // indice del token analizado para ubicarlo en la lista y posteriormente en la
                   // tabla
        String produccion;// almacena la produccion que se encuentra en tabla[][]

        // Ciclo while que se detiene solo si la pila esta vacia y si hay errores
        while (!pila.isEmpty() && !hayErrores) {
            // inicializacion de las variables con los valores que se tienen al momento
            index = terminales.indexOf(preanalisis.tipo.toString());
            tope = pila.peek().toString();

            // Caso 1: Tope no terminal
            if (nterminales.contains(tope)) {
                // si no hay produccion en las intersecciones del token con el tope significa error
                if (Objects.isNull(tabla[nterminales.indexOf(tope)][index])) {
                    hayErrores = true;
                    break;
                } else {
                    // Caso Epsilo: POP
                    if (tabla[nterminales.indexOf(tope)][index].equals("EPSILON")) {
                        pila.pop();
                    } else {
                        // Valor que este en la tabla
                        produccion = tabla[nterminales.indexOf(tope)][index];
                        // Rellenado de pila
                        pila.pop();
                        String[] split = produccion.split(" "); // Fragmentación
                        for (int j = split.length - 1; j >= 0; j--) {
                            pila.push(split[j]);
                        }
                    }
                }
            }
            // Caso 2: Tope de la pila terminal
            if (terminales.contains(tope)) {
                // si el tope de la pila es terminal y coincide con el token analizado, se hace
                // pop y se recorren ambos "apuntadores" (preanalisis y toé)
                if (tope.equals(preanalisis.tipo.toString())) {
                    i++;
                    pila.pop();
                    if (tope.equals("EOF"))
                        hayErrores = false;
                    else
                        preanalisis = tokens.get(i);
                } else {
                    // si no coinciden error
                    hayErrores = true;
                }
            }
        }
        // chequeo de la bandera, pila vacia indica analisis completado sin errores
        if (pila.isEmpty() && hayErrores == false) {
            System.out.println("CONSULTA VALIDA");
        } else {
            System.out.println("SE ENCONTRARON ERRORES");
        }
        return false;

    }

}
