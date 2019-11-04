import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Socket_Server {
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    private Scanner scanner = new Scanner(System.in);
    private String COMANDO_TERMINACION = "salir";


    //Se configura la conexion
    private void comenzarConexion(int puerto) {
        try {
            ServerSocket serverSocket = new ServerSocket(puerto);
            System.out.println("Esperando conexión entrante en el puerto " + puerto + "...");
            socket = serverSocket.accept();
            System.out.println("Conexión establecida \n");
        } catch (Exception e) {
            System.out.println("Error al levantar conexion: " + e.getMessage());
            System.exit(1);
        }
    }
    
    //Configuracion de Flujo de Datos
    private void flujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            System.out.println("Error de IO");
        }
    }

    //Recibir Datos desde el cliente
    private void recibirDatos() {
        String st;
        try {
            do {
                st = bufferDeEntrada.readUTF();
                System.out.println("\nCliente => " + st);
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException ignored) {
        }
    }


    //Enviar datos al Cliente
    private void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            System.out.println("Error al enviar:" + e.getMessage());
        }
    }

    //Lee datos ingresados por el teclado
    private void escribirDatos() {
        String entrada;
        while (true) {                       //Se ejecuta por siempre
            entrada = scanner.nextLine();
            if(!entrada.isEmpty()){
                enviar(entrada);   //Se envia lo ingresado
            }

        }
    }

    //Termina la conexion existente
    private void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
        } catch (IOException e) {
          System.out.println("Error al cerrar conexion: " + e.getMessage());
        } finally {
            System.out.println("Conversación finalizada");
            System.exit(0);

        }
    }

    //Thread que se encarga de recibir los datos continuamente
    private void ejecutarConexion(int puerto) {
        new Thread(() -> {
            comenzarConexion(puerto);
            flujos();
            while (true) {
                try {
                    recibirDatos();
                } finally {
                    cerrarConexion();
                }
            }
        }).start();
    }


    public static void main(String[] args) throws IOException {

        Socket_Server s = new Socket_Server();  //Instanciar clase principal
        Scanner sc = new Scanner(System.in);    //Input del teclado

        System.out.println("Ingresar el puerto (5050 por defecto): ");
        String puerto = sc.nextLine().length() <= 0 ? "5050" : sc.nextLine();
        s.ejecutarConexion(Integer.parseInt(puerto));
        s.escribirDatos();
    }
}