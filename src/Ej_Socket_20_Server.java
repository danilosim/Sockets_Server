import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Ej_Socket_20_Server {
    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    Scanner escaner = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()";

    
    //configurar la conexión en base al puerto elegido
    public void comenzarConexion(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("Esperando conexión entrante en el puerto " + String.valueOf(puerto) + "...");
            socket = serverSocket.accept();
            System.out.println("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
        } catch (Exception e) {
            System.out.println("Error en levantarConexion(): " + e.getMessage());
            System.exit(0);
        }
    }
    
    //configurar streams (flujo de bytes)
    public void flujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            System.out.println("Error en la apertura de flujos");
        }
    }

    public void recibirDatos() {
        String st = "";
        try {
            do {
                st = (String) bufferDeEntrada.readUTF();
                System.out.println("\n[Cliente] => " + st);
                System.out.print("\n[YO] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {
            cerrarConexion();
        }
    }


    //envía datos a través del socket
    public void enviar(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();      //forzar envío
        } catch (IOException e) {
            System.out.println("Error en enviar(): " + e.getMessage());
        }
    }

 
    public void escribirDatos() {
        while (true) {                       //ejecutar por siempre
            System.out.print("[YO] => ");
            enviar(escaner.nextLine());   //enviar lo que se lee por teclado
        }
    }

    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
        } catch (IOException e) {
          System.out.println("Excepción en cerrarConexion(): " + e.getMessage());
        } finally {
            System.out.println("Conversación finalizada....");
            System.exit(0);

        }
    }

    public void ejecutarConexion(int puerto) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                comenzarConexion(puerto);
                flujos();
                while (true) {
                    try {
                        recibirDatos();
                    } finally {
                        cerrarConexion();
                    }
                }
            }
        });
        hilo.start();
    }

    
    //Hilo Principal------------------------------------------------------------
    //--------------------------------------------------------------------------
    public static void main(String[] args) throws IOException {

        Ej_Socket_20_Server s = new Ej_Socket_20_Server();  //Instanciar clase ppal
        Scanner sc = new Scanner(System.in);                //teclado  

        System.out.println("Ingresar el puerto [5050 por defecto (enter)]: ");
        String puerto = sc.nextLine();
        if (puerto.length() <= 0) puerto = "5050";
        s.ejecutarConexion(Integer.parseInt(puerto));   //acá comienza todo
        s.escribirDatos();
    }
}