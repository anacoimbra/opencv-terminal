/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author aninh
 */
public class Questao3 {
    int MAXFILES = 10;
    Scanner in;
    String[] arquivos;
    List<Mat> images;
    Mat ruido;

    public Questao3() throws IOException{
        this.in = new Scanner(System.in);
        this.arquivos = new String[MAXFILES];
        this.images = new ArrayList<>();
        for(int i = 0; i < MAXFILES; i++){
            arquivos[i] = "(" + (i + 1) + ").jpg";
        }

        desvioPadrao();
        criaGrafico();
        media();

        return;
    }

    void desvioPadrao(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        /**
         * Array temporario que irá armazenar os valores de um pixel
         * para todas as imagens de entrada.
         */
        double[] tmp = new double[arquivos.length];

        /**
         * Tranforma imagens em matrizes em escala de cinza
         */
        for(String s : arquivos){
            images.add(Imgcodecs.imread(s, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE));
        }

        /**
         * Matriz que irá armazenar os valores de desvio padrão
         */
        double[][] stdDev = new double[images.get(0).rows()][images.get(0).cols()];

        /**
         * Calculo do desvio padrão para cada pixel de todas as imagens.
         */
        for(int i = 0; i < images.get(0).rows(); i++){
            for(int j = 0; j < images.get(0).cols(); j++){
                for(int k = 0; k < arquivos.length; k ++){
                    tmp[k] = images.get(k).get(i, j)[0];
                }
                double tmpDev = Math.sqrt(somatorio(tmp) / arquivos.length);
                stdDev[i][j] = tmpDev;

            }
        }

        /**
         * Cria uma matriz opencv do tipo escala de cinza
         */
        ruido = new Mat(new Size((int)images.get(0).cols(), (int)images.get(0).rows()),CvType.CV_8UC1);
        /**
         * Matriz que ira receber a matriz de desvio padrão normalizada
         * Maior ruído = 255
         * Ausência de ruído = 0
         */
        double[][] d = normalizacao(stdDev);
        /**
         * Salva o resultado na imagem ruido.jpg
         */
        Imgcodecs.imwrite("ruido.jpg",ruido);
        showResult("ruido.jpg");
    }

    /**
     * Metodo para normalizar uma matriz
     * maior valor = 255
     * menor valor = 0
     * @param matriz
     * @return
     */
    public double[][] normalizacao(double[][] matriz){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        /**
         * Maior e menor valor da matriz
         */
        double max = maxValue(matriz);
        double min = minValue(matriz);

        /**
         * Calcula a distancia entre os valores, que será o valor
         * utilizado para normalizar
         */
        double dis = 255 / (max - min);

        /**
         * Matriz resultante = copia da original
         */
        double[][] result = matriz.clone();

        /**
         * Normalizacao - para cada pixel da matriz, multiplica-se o valor dis.
         */
        for(int i = 0; i < result.length; i++){
            for(int j = 0; j < result[0].length; j++){
                result[i][j] *= dis;
                ruido.put(i, j, result[i][j]);
            }
        }

        /**
         * Restorna o resultado
         */
        return result;
    }

    /**
     * Metodo que soma os valores de um array
     * @param array
     * @return
     */
    public double somatorio(double[] array){
        double sum = 0;
        double media = media(array);

        for(double d : array){
            double x = Math.pow((d - media), 2);
            sum += x;
        }
        return sum;
    }

    /**
     * Metodo que calcula a media de um array
     * @param array
     * @return
     */
    public double media(double[] array){
        double sum = 0;
        for(double d : array){
            sum += d;
        }
        return sum / array.length;
    }

    /**
     * Metodo que retorna o maior valor da matriz
     * @param matriz
     * @return double max
     */
    public double maxValue(double[][] matriz){
        double max = Double.MIN_VALUE;
        for(double[] linha : matriz){
            for(double d : linha){
                if(d > max){
                    max = d;
                }
            }
        }
        return max;
    }

    /**
     * Metodo que retorna o menor valor de uma matriz
     * @param matriz
     * @return double min
     */
    public double minValue(double[][] matriz){
        double min = Double.MAX_VALUE;
        for(double[] linha : matriz){
            for(double d : linha){
                if(d < min){
                    min = d;
                }
            }
        }
        return min;
    }

    /**
     * Metodo que cria os dados que serão base para o grafico da média dos pixels
     * @return dataset
     */
    private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i = 0; i < images.get(0).cols(); i ++){
            double med1 = 0;
            double med2 = 0;
            for(int x = 0; x < images.size(); x++){
                med1 += (images.get(x).get(0, i)[0]);
                med2 += (images.get(x).get(0, i)[0]);
            }
            med1 /= images.size();
            med2 /= images.size();

            dataset.addValue(med1  - ruido.get(0, i)[0],"Media - Ruído", "Pixel" + i);
            dataset.addValue(med2 + ruido.get(0, i)[0],"Media + Ruído", "Pixel" + i);
        }

        return dataset;

    }

    /**
     * Metodo cria e exibe o gráfico da media dos pixels das imagens resultantes
     * @throws IOException
     */
    public void criaGrafico() throws IOException {
        CategoryDataset cds = createDataset();
        String titulo = "Média da Primeira linha de pixels";
        String eixoy = "Valores";
        String txt_legenda = "Pixels";
        boolean legenda = true;
        boolean tooltips = true;
        boolean urls = true;
        JFreeChart graf = ChartFactory.createLineChart(titulo, txt_legenda, eixoy, cds);
        File lineChart = new File( "grafico.jpeg" );
        ChartUtilities.saveChartAsJPEG(lineChart ,graf, 512 ,256);
        showResult("grafico.jpeg");
    }

    public static void showResult(String url) {
        JFrame frame = new JFrame(url);
        frame.getContentPane().add(new JLabel(new ImageIcon(url)));
        frame.pack();
        frame.setVisible(true);
    }

    void media(){
        /**
         * Core.mean(Mat) retorna o valor da media dos valores
         */
        Scalar d = Core.mean(ruido);
        System.out.println("Média do ruído: " + d.val[0]);
    }
}
