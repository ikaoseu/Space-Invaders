/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author Jorge Cisneros
 */
public class VentanaJuego extends javax.swing.JFrame {

    static int ANCHOPANTALLA = 600;
    static int ALTOPANTALLA = 450;
    
    //numero de marcianos que van a aparecer.
    int filas = 4;
    int columnas = 6;
    int contador = 0;
    int disparoLaser = 1;
    //imagen para cargar la plantilla de murcianos.
    BufferedImage plantilla = null;
    Image [] imagenes = new Image[30];
    
    
    BufferedImage buffer = null;
    
    Nave miNave = new Nave();
    Disparo miDisparo = new Disparo();
    //Marciano miMarciano = new Marciano();
    Marciano [][] listaMarcianos = new Marciano [filas] [columnas]; 
    boolean direccionMarcianos = false;
    
    //inicializo el array de murcianos
    
  
    
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            bucleDelJuego();
        }
    });
    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
               
               AudioClip sonido;
               sonido = java.applet.Applet.newAudioClip(getClass().getResource("/sonidos/ambience.wav"));
               sonido.play();
        try {
            plantilla = ImageIO.read(getClass().getResource("/imagenes/invaders2.png"));
        } catch (IOException ex) {
            Logger.getLogger(VentanaJuego.class.getName()).log(Level.SEVERE, null, ex);
        }
        //cargo las imagenes de la plantilla de forma individual en cada imagen del array
         for(int i=0; i<5; i++){
            for(int j=0; j<4; j++){
                imagenes[i * 4 + j] = plantilla.getSubimage(j * 64, i * 64, 64, 64);
                imagenes[i * 4 + j] = imagenes[i * 4 + j].getScaledInstance(32,32,Image.SCALE_SMOOTH);
            }
        }
         
        for(int j=0; j<4; j++){
            imagenes[20 + j] = plantilla.getSubimage(j * 64,5 * 64, 64, 32);
        }
        
        
        
        setSize(ANCHOPANTALLA, ALTOPANTALLA);
        buffer = (BufferedImage) jPanel1.createImage(ANCHOPANTALLA, ALTOPANTALLA);
        buffer.createGraphics();
        
        temporizador.start();
        
        //inicializo la posición inicial de la nave
        miNave.x = ANCHOPANTALLA /2 - miNave.imagen.getWidth(this) / 2;
        miNave.y = ALTOPANTALLA - miNave.imagen.getHeight(this)  - 40; 
        
        //inicializo el array de marcianos
        
          for(int i=0; i<filas; i++){
        for(int j=0; j<columnas; j++){
            listaMarcianos[i][j] = new Marciano();
            listaMarcianos[i][j].imagen1 = imagenes[2*i];
            listaMarcianos[i][j].imagen2 = imagenes[2*i+1];
            listaMarcianos[i][j].x = j*(15 + listaMarcianos[i][j].imagen1.getWidth(null));
            listaMarcianos[i][j].y = i*(10 + listaMarcianos[i][j].imagen1.getHeight(null));
            
        }
}
        
    }
    
    private void bucleDelJuego(){
        //se encarga del redibujado de los objetos en el jPanel1
        //primero borro todo lo que hay en el buffer
        contador++;   
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, ANCHOPANTALLA, ALTOPANTALLA);
        
        ///////////////////////////////////////////////////////
        //redibujaremos aquí cada elemento
        g2.drawImage(miDisparo.imagen, miDisparo.x, miDisparo.y, null);
        g2.drawImage(miNave.imagen, miNave.x, miNave.y, null);
        
        pintaMarcianos(g2);
        gameOver();
        chequeaImpacto();
        miNave.mueve();
        miDisparo.mueve();
        /////////////////////////////////////////////////////////////
        //*****************   fase final, se dibuja ***************//
        //*****************   el buffer de golpe sobre el Jpanel***//
        
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);
        
    }
    
    private void chequeaImpacto(){
        int alto = miDisparo.imagen.getHeight(null);
        int ancho = miDisparo.imagen.getWidth(null);
        
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double();
        
        rectanguloDisparo.setFrame(miDisparo.x,
                                   miDisparo.y,
                                   alto,
                                   ancho);
         for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
               if(listaMarcianos[i][j].vivo){  
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].x, listaMarcianos[i][j].getY(),
                                                listaMarcianos[i][j].imagen1.getHeight(null),
                                                listaMarcianos[i][j].imagen1.getWidth(null)
                                                );
                    if(rectanguloDisparo.intersects(rectanguloMarciano)){
                         AudioClip sonido;
                         sonido = java.applet.Applet.newAudioClip(getClass().getResource("/sonidos/bicho1.wav"));
                         sonido.play();
                        listaMarcianos[i][j].vivo = false;
                        miDisparo.posicionaDisparo(miNave);
                        miDisparo.y = 1000;
                        miDisparo.disparado = false;
                    }

                }
            }
         }
    }
    
    private void gameOver(){
        int alto = miNave.imagen.getHeight(null);
        int ancho = miNave.imagen.getWidth(null);
        
        Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        Rectangle2D.Double rectanguloNave = new Rectangle2D.Double();
            
            rectanguloNave.setFrame(miNave.x,
                                    miNave.y,
                                    alto,
                                    ancho);
        
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
               if(listaMarcianos[i][j].vivo){  
                    rectanguloMarciano.setFrame(listaMarcianos[i][j].x, listaMarcianos[i][j].getY(),
                                                listaMarcianos[i][j].imagen1.getHeight(null),
                                                listaMarcianos[i][j].imagen1.getWidth(null)
                                                );
                    if(rectanguloNave.intersects(rectanguloMarciano)){
                        //cambiar pantalla por inal del juego.
                        listaMarcianos[i][j].vivo = false;
                        miDisparo.posicionaDisparo(miNave);
                        miDisparo.y = 1000;
                        miDisparo.disparado = false;
                    }

                }
            }
         }
    }
    
    private void cambiaDireccion(){
         for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                listaMarcianos[i][j].setY(listaMarcianos[i][j].getY() + listaMarcianos[0][0].imagen1.getHeight(null));
                listaMarcianos[i][j].setvX(listaMarcianos[i][j].getvX() * -1);
                
            }
            }
    }
    private void pintaMarcianos(Graphics2D _g2){
        int anchoMarciano = listaMarcianos[0][0].imagen1.getWidth(null);
        for(int i=0; i<filas; i++){
            for(int j=0; j<columnas; j++){
                if(listaMarcianos[i][j].vivo){               
                    listaMarcianos[i][j].mueve();
                    //chequeo si el marciano ha chocado contra la pared para cambiar la direccion
                    if(listaMarcianos[i][j].x + anchoMarciano == ANCHOPANTALLA || (listaMarcianos[i][j].x == 0)){
                        direccionMarcianos = true;
                    }

                    if(contador < 50){
                        _g2.drawImage(listaMarcianos[i][j].imagen1,
                                      listaMarcianos[i][j].x,
                                      listaMarcianos[i][j].getY(),
                                      null);
                    }else if (contador < 100){
                        _g2.drawImage(listaMarcianos[i][j].imagen2,
                                      listaMarcianos[i][j].x, listaMarcianos[i][j].getY(),
                                      null);
                    }
                    else contador = 0;
            }
            }
        }
        if(direccionMarcianos){
            cambiaDireccion();
            direccionMarcianos = false;
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoInv.jpg"))); // NOI18N
        jLabel1.setText("jLabel1");
        jLabel1.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 99, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 284, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
       switch (evt.getKeyCode()){
           case KeyEvent.VK_LEFT:
               miNave.setPulsadoIzquierda(true);
               break;
           case KeyEvent.VK_RIGHT:
               miNave.setPulsadoDerecha(true); 
               break;
           case KeyEvent.VK_SPACE:
               if(disparoLaser == 1){
                   AudioClip sonido;
                   sonido = java.applet.Applet.newAudioClip(getClass().getResource("/sonidos/lasrhit1.wav"));
                   sonido.play();
                   disparoLaser = 2;
               }else{
                   if(disparoLaser == 2){
                       AudioClip sonido;
                       sonido = java.applet.Applet.newAudioClip(getClass().getResource("/sonidos/lasrhit2.wav"));
                       sonido.play();
                       disparoLaser = 3;
                   }else{
                       if(disparoLaser == 3){
                           AudioClip sonido;
                           sonido = java.applet.Applet.newAudioClip(getClass().getResource("/sonidos/lasrhit3.wav"));
                           sonido.play();
                           disparoLaser = 4;
                       }else{
                             AudioClip sonido;
                             sonido = java.applet.Applet.newAudioClip(getClass().getResource("/sonidos/lasrhit4.wav"));
                             sonido.play();
                             disparoLaser = 1;
                       }
                   }
               }
               miDisparo.posicionaDisparo(miNave); 
               miDisparo.disparado = true; 
               break;
       }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
       switch (evt.getKeyCode()){
           case KeyEvent.VK_LEFT: miNave.setPulsadoIzquierda(false) ; break;
           case KeyEvent.VK_RIGHT: miNave.setPulsadoDerecha(false); break;
       }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
