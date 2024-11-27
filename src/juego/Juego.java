package juego;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 *
 * @author oneiber
 */
public class Juego {

    //atributos
    static JFrame ventana;

    //presentacion
    JPanel panelPresentacion;
    JButton iniciar;
    JLabel fondoPresentacion;
    ImageIcon imagenFondoPres;

    //menu
    JPanel panelMenu;
    JButton botones[];
    JLabel fondoMenu;
    ImageIcon imagenFondoMenu;

    //juego
    static JPanel panelJuego;
    JLabel fondoJuego;
    ImageIcon imagenFondoJuego;
    static int mat[][];
    static JLabel matriz[][];
    int px;
    int py;
    String jugador;
    JLabel nombre;
    int puntos;
    JLabel records;
    int abajo;
    int arriba;
    int izq;
    int der;
    Timer timer;

    //fantasmas
    Fantasmas fantasma1;
    Fantasmas fantasma2;
    Fantasmas fantasma3;
    static int matAux[][];

    public class BotonRedondeado extends JButton {

        private int radius;

        public BotonRedondeado(String text, int radius) {
            super(text);
            this.radius = radius;
            setFocusPainted(false); // Quitar borde de enfoque
            setContentAreaFilled(false); // Permitir personalización completa del botón
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo del botón
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Borde del botón
            g2.setColor(getForeground());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            super.paintComponent(g);
        }

        @Override
        public void paintBorder(Graphics g) {
            // No hace falta pintar el borde externo aquí
        }

        @Override
        public boolean contains(int x, int y) {
            // Verificar si el punto está dentro del área redondeada
            int w = getWidth();
            int h = getHeight();
            return (Math.pow(x - w / 2.0, 2) + Math.pow(y - h / 2.0, 2)) <= Math.pow(Math.min(w, h) / 2.0, 2);
        }
    }

    public Juego() {

        ventana = new JFrame("PACMAN");
        ventana.setSize(700, 700);
        ventana.setLayout(null);
        ventana.setLocationRelativeTo(null);
        ventana.setResizable(false);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panelPresentacion = new JPanel();
        panelPresentacion.setLayout(null);
        panelPresentacion.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        panelPresentacion.setVisible(true);
        panelPresentacion.setOpaque(false); // Para que no haya fondo visible

        fondoPresentacion = new JLabel(new ImageIcon("imagenes/fondoPresentacion.gif"));
        fondoPresentacion.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        fondoPresentacion.setHorizontalAlignment(SwingConstants.CENTER); // Centrado horizontal
        fondoPresentacion.setVerticalAlignment(SwingConstants.CENTER);   // Centrado vertical
        fondoPresentacion.setVisible(true);

        panelPresentacion.add(fondoPresentacion, 0); // Asegúrate de agregarlo al panel correctamente               

        iniciar = new BotonRedondeado("Iniciar", 30); // Radio de 30 para bordes redondeados
        iniciar.setBounds((ventana.getWidth() - 200) / 2, 550, 200, 70);
        iniciar.setFont(new Font("Times New Roman", Font.BOLD, 40));
        iniciar.setVisible(true);
        iniciar.setBackground(new Color(251, 123, 38));
        iniciar.setForeground(Color.black);

        //Agregar el botón después del fondo
        panelPresentacion.add(iniciar, 1);

        panelPresentacion.add(iniciar, 0);
        fondoPresentacion = new JLabel();
        fondoPresentacion.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        String gifPath = "imagenes/fondoPresentacion.gif";

        //menu
        botones = new JButton[5];
        for (int i = 0; i < botones.length; i++) {
            botones[i] = new JButton();
        }

        iniciar.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("iniciar");
                menu();
                eventoMenu();
            }

        });

        //juego
        mat = new int[15][15];
        mat = tablero(1);
        //matAux = tablero(1);
        matriz = new JLabel[15][15];
        matAux = new int[15][15];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                matriz[i][j] = new JLabel();
                matAux[i][j] = mat[i][j];
            }

        }

        // Agregar fantasmas de diferentes colores
        mat[2][2] = 7; // Fantasma azul
        mat[4][4] = 8; // Fantasma rojo
        mat[6][6] = 9; // Fantasma rosado

        px = 1;
        py = 1;
        mat[px][py] = 3;

        abajo = 0;
        arriba = 0;
        izq = 0;
        der = 0;

        ventana.add(panelPresentacion);

        ventana.setVisible(true);

    }//fin constructor
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void limpiarFantasmasEstaticos() {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (mat[i][j] == 7 || mat[i][j] == 8 || mat[i][j] == 9) {
                    mat[i][j] = 0; // Limpia cualquier fantasma estático
                }
            }
        }
    }

    public void jugar() {

        // Reinicia la matriz y coloca los valores iniciales
        mat = tablero(1); // Restaurar el tablero original
        matAux = tablero(1); // También restaurar la matriz auxiliar

        // Reiniciar posición inicial del Pacman
        px = 1;
        py = 1;
        mat[px][py] = 3; // Pacman está en su posición inicial

        // Limpia el panelJuego antes de reconstruirlo
        if (panelJuego != null) {
            ventana.remove(panelJuego);
        }

        panelMenu.setVisible(false);

        panelJuego = new JPanel();
        panelJuego.setLayout(null);
        panelJuego.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        panelJuego.setVisible(true);

        fondoJuego = new JLabel();
        fondoJuego.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        imagenFondoJuego = new ImageIcon("imagenes/fondoJugar.png"); // Cambia aquí la imagen
        imagenFondoJuego = new ImageIcon(imagenFondoJuego.getImage().getScaledInstance(ventana.getWidth(), ventana.getHeight(), Image.SCALE_DEFAULT));
        fondoJuego.setIcon(imagenFondoJuego);
        fondoJuego.setVisible(true);
        panelJuego.add(fondoJuego, 0);

        int offsetVertical = 30; // Ajusta este valor para moverlo hacia arriba
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                matriz[i][j].setIcon(new ImageIcon("imagenes/" + mat[i][j] + ".png"));
                matriz[i][j].setBounds(120 + (i * 30), 80 + (j * 30), 30, 30); // Ajusta el offset vertical
                matriz[i][j].setVisible(true);
                panelJuego.add(matriz[i][j], 1); // Asegúrate de agregarlo en una capa superior al fondo
            }
        }

        nombre = new JLabel("JUGADOR: " + jugador);
        nombre.setBounds(25, 18, 150, 30);
        nombre.setForeground(Color.white);
        nombre.setVisible(true);
        panelJuego.add(nombre, 0);

        puntos = 0;
        records = new JLabel("Puntos: " + puntos);
        records.setBounds(ventana.getWidth() - (150 + 20) - 55, 18, 150, 30);
        records.setVisible(true);
        records.setForeground(Color.white);
        panelJuego.add(records, 0);
        ventana.add(panelJuego);

        limpiarFantasmasEstaticos(); // Limpiar fantasmas estáticos
        fantasma1 = new Fantasmas(12, 13, 7); // Fantasma azul
        fantasma2 = new Fantasmas(13, 13, 8); // Fantasma rojo
        fantasma3 = new Fantasmas(13, 12, 9); // Fantasma rosado
        ventana.add(panelJuego);

        mover();

    }

    public static void pintarMatriz() {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                matriz[i][j].setIcon(new ImageIcon("imagenes/" + mat[i][j] + ".gif"));
                matriz[i][j].setBounds(120 + (i * 30), 80 + (j * 30), 30, 30);
                matriz[i][j].setVisible(true);
                panelJuego.add(matriz[i][j], 0);
            }
        }
    }

    public void mover() {

        timer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (arriba == 1 && (mat[px][py - 1] == 1 || mat[px][py - 1] == 0)) {
                    if (mat[px][py - 1] == 1) {
                        puntos = puntos + 5;
                        records.setText("Puntos: " + puntos);
                    }
                    mat[px][py] = 0;
                    matAux[px][py] = mat[px][py]; //esto es nuevo
                    py = py - 1;
                    mat[px][py] = 3;
                    pintarMatriz();

                }
                if (abajo == 1 && (mat[px][py + 1] == 1 || mat[px][py + 1] == 0)) {
                    if (mat[px][py + 1] == 1) {
                        puntos = puntos + 5;
                        records.setText("Puntos: " + puntos);
                    }
                    mat[px][py] = 0;
                    matAux[px][py] = mat[px][py]; //esto es nuevo
                    py = py + 1;
                    mat[px][py] = 3;
                    pintarMatriz();

                }
                if (izq == 1 && (mat[px - 1][py] == 1 || mat[px - 1][py] == 0)) {
                    if (mat[px - 1][py] == 1) {
                        puntos = puntos + 5;
                        records.setText("Puntos: " + puntos);
                    }
                    mat[px][py] = 0;
                    matAux[px][py] = mat[px][py]; //esto es nuevo
                    px = px - 1;
                    mat[px][py] = 3;
                    pintarMatriz();

                }
                if (der == 1 && (mat[px + 1][py] == 1 || mat[px + 1][py] == 0)) {
                    if (mat[px + 1][py] == 1) {
                        puntos = puntos + 5;
                        records.setText("Puntos: " + puntos);
                    }
                    mat[px][py] = 0;
                    matAux[px][py] = mat[px][py]; //esto es nuevo
                    px = px + 1;
                    mat[px][py] = 3;
                    pintarMatriz();

                }
                int enc = 0;
                for (int i = 0; i < mat.length && enc == 0; i++) {
                    for (int j = 0; j < mat.length && enc == 0; j++) {
                        if (mat[i][j] == 1) {
                            enc = 1;
                        }
                    }
                }
                if (enc == 0) {
                    JOptionPane.showMessageDialog(ventana, "FELICITACIONES GANO");
                    panelJuego.setVisible(false);
                    panelMenu.setVisible(true);
                    timer.stop();
                }

                //matar pacman
                if (mat[px][py + 1] == 7 || mat[px][py - 1] == 7
                        || mat[px - 1][py] == 7 || mat[px + 1][py] == 7
                        || mat[px][py + 1] == 8 || mat[px][py - 1] == 8
                        || mat[px - 1][py] == 8 || mat[px + 1][py] == 8
                        || mat[px][py + 1] == 9 || mat[px][py - 1] == 9
                        || mat[px - 1][py] == 9 || mat[px + 1][py] == 9) {

                    // Detener los timers de los fantasmas
                    if (fantasma1 != null) {
                        fantasma1.timer.stop();
                    }
                    if (fantasma2 != null) {
                        fantasma2.timer.stop();
                    }
                    if (fantasma3 != null) {
                        fantasma3.timer.stop();
                    }

                    // Mostrar mensaje de muerte
                    JOptionPane.showMessageDialog(ventana, "ESTAS MUERTO");

                    // Volver al menú
                    panelJuego.setVisible(false);
                    panelMenu.setVisible(true);
                    timer.stop(); // Detener el juego principal
                }
            }

        });
        timer.start();
        ventana.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    System.out.println("tecla hacia arriba");
                    if (mat[px][py - 1] == 1 || mat[px][py - 1] == 0) {
                        arriba = 1;
                        abajo = 0;
                        izq = 0;
                        der = 0;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    System.out.println("tecla hacia abajo");
                    if (mat[px][py + 1] == 1 || mat[px][py + 1] == 0) {
                        arriba = 0;
                        abajo = 1;
                        izq = 0;
                        der = 0;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    System.out.println("tecla hacia izquierda");
                    if (mat[px - 1][py] == 1 || mat[px - 1][py] == 0) {
                        arriba = 0;
                        abajo = 0;
                        izq = 1;
                        der = 0;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    System.out.println("tecla hacia derecha");
                    if (mat[px + 1][py] == 1 || mat[px + 1][py] == 0) {
                        arriba = 0;
                        abajo = 0;
                        izq = 0;
                        der = 1;
                    }
                }

                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });

    }

    public int[][] tablero(int opcion) {

        int[][] aux1 = new int[15][15];
        if (opcion == 1) {

            int aux[][] = {
                {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2},
                {2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 2, 2},
                {2, 1, 2, 2, 1, 2, 2, 1, 1, 2, 2, 1, 1, 1, 2},
                {2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2},
                {2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2},
                {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},};

            return aux;
        }
        if (opcion == 2) {
            int aux[][] = {
                {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {2, 2, 2, 2, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2},
                {2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2},
                {2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2},
                {2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2},
                {2, 2, 2, 2, 2, 1, 1, 1, 2, 2, 2, 1, 2, 2, 2},
                {2, 1, 2, 2, 1, 2, 2, 1, 1, 2, 2, 1, 1, 1, 2},
                {2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2},
                {2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2},
                {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},};
            return aux;
        }
        if (opcion == 3) {
            int aux[][] = {
                {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2},
                {2, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 2, 2},
                {2, 1, 2, 2, 1, 2, 2, 1, 1, 2, 2, 1, 1, 1, 2},
                {2, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2},
                {2, 2, 2, 1, 2, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {2, 1, 2, 1, 1, 1, 2, 2, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 1, 2},
                {2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
                {2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2},
                {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},};
            return aux;
        }
        return aux1;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public void menu() {

        panelPresentacion.setVisible(false);

        panelMenu = new JPanel();
        panelMenu.setLayout(null);
        panelMenu.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        panelMenu.setVisible(true);

        fondoMenu = new JLabel();
        fondoMenu.setBounds(0, 0, ventana.getWidth(), ventana.getHeight());
        imagenFondoMenu = new ImageIcon("imagenes/fondoMenu.png");
        imagenFondoMenu = new ImageIcon(imagenFondoMenu.getImage().getScaledInstance(ventana.getWidth(), ventana.getHeight(), Image.SCALE_DEFAULT));
        fondoMenu.setIcon(imagenFondoMenu);
        fondoMenu.setVisible(true);
        panelMenu.add(fondoMenu);

        botones[0].setText("JUGAR");
        botones[1].setText("Crear tablero");
        botones[2].setText("Records");
        botones[3].setText("Cargar tablero");
        botones[4].setText("SALIR");

        int botonAncho = 300; // Ancho de los botones
        int botonAlto = 60;   // Alto de los botones
        int espacioVertical = 30; // Espacio entre botones
        int yInicio = 100; // Posición vertical inicial
        int desplazamientoDerecha = 70; // Mover los botones a la derecha

        for (int i = 0; i < botones.length; i++) {
            int xPos = (ventana.getWidth() - botonAncho) / 2 + desplazamientoDerecha; // Centrado y desplazado a la derecha
            int yPos = yInicio + i * (botonAlto + espacioVertical); // Espaciado vertical
            botones[i].setBounds(xPos, yPos, botonAncho, botonAlto); // Establecer dimensiones y posición
            botones[i].setVisible(true);
            botones[i].setFont(new Font("Times New Roman", Font.BOLD, 35)); // Ajustar tamaño de letra
            botones[i].setBackground(Color.orange);
            botones[i].setFocusPainted(false); // Eliminar borde de enfoque
            botones[i].setVisible(true); // Asegurarse de que sean visibles

            // Agregar botón al panel
            panelMenu.add(botones[i]);
        }

        // Asegurar que los botones estén encima del fondo
        panelMenu.setComponentZOrder(fondoMenu, panelMenu.getComponentCount() - 1);

        ventana.add(panelMenu);

    }//fin del menu

    public void eventoMenu() {

        //boton jugar
        botones[0].addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("jugar");
                //pedir nombre
                jugador = JOptionPane.showInputDialog(ventana, "Nombre del jugador", "Escribe aqui");

                // Verificar si se presionó "Cancel"
                if (jugador == null) {
                    System.out.println("El jugador canceló el ingreso del nombre.");
                    return; // Salir sin iniciar el juego
                }
                while (jugador == null || jugador.compareTo("Escribe aqui") == 0 || jugador.compareTo("") == 0) {
                    jugador = JOptionPane.showInputDialog(ventana, "Debes ingresar usuario", "Escribe aqui");

                    if (jugador == null) { // Verificar si canceló nuevamente
                        System.out.println("El jugador canceló el ingreso del nombre.");
                        return;
                    }
                }
                jugar();

            }

        });

        //boton crear tablero
        botones[1].addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("crear tablero");

            }

        });

        //boton records
        botones[2].addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("records");

            }

        });

        //cargar tablero
        botones[3].addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("cargar tablero");

            }

        });

        //salir
        botones[4].addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                System.out.println("SALIR");

                JOptionPane.showMessageDialog(ventana, "Gracias por jugar, vuelva pronto (っ◕‿◕)っ ♥", "¡Hasta luego!", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }

        });

    }

}
