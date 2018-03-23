                                            
package jforex;
import java.math.*;
//import java.math.*;
import java.util.*;
import java.text.*;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.dukascopy.api.*;

@RequiresFullAccess
public class NA5_bi_tempo4 implements IStrategy {
    private Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
    //private Calendar calendar = Calendar.getInstance();
    private IContext context = null;
    private IConsole console;
    private IEngine engine = null;
    private IChart chart = null;
    private IHistory history;
    private IIndicators indicators = null;
    private boolean posCreated = false;
    private int tagCounter = 0;
    protected double equity;
    protected double cuenta;
    protected double old_cuenta=0;
    //private double Lots=0.01;
    private int []estado_compra = new int [11];
    private int []estado_venta = new int [11];
    private int []estado_old = new int [11];
    private int []estado_oldv = new int [11];
    private int []tendencia = new int [11];
    private int []corte_up = new int [11];
    private int []corte_do = new int [11];
    private int []giroup =new int[11];
    private int []girodo = new int[11];
    private int []instrumento_activo= new int [11];
    private double []will = new double [11];
    private double []kcup = new double[11];
    private double []kcdo = new double[11]; 
    private int []arranque = new int[11];   
    private double []stop_venta = new double[11];
    private double []stop_compra = new double[11]; 
    private String []nombrefichero = new String[11]; 
    
    // parametros
    private int []PeriodoWill = new int [11];
    private int []PeriodoStarc = new int[11];
    private double []FactorAtr = new double[11];
    private int []StopLoss = new int[11];
    private int []TakeProfit = new int[11];
    private double []posicion = new double[11];
   private double []Point = new double[11];
   private double []piv_R2 = new double[11];
   private double []piv_S2 = new double[11];
  private int []pivot_revasado= new int[11];
   private boolean []permite_entrada= new boolean [11];
    private int i;
    private double preu;
    //private double Slippage=3;
   // private double Point;
    private double contador=0;
    private int findia=0;
    private double will2=0;
    private double elminimo;
    private double elmaximo;
    private double elcorte;
   // private int estado_old=1;
   // variables para las estadisticas
   private int num_trades=0;
   private int ganadoras=0;
   private int perdedoras=0;
   private double profit_pips;
   private double profit_ppips;
   private double profit_ganadoras=0;
   private double profit_perdedoras=0;
   private double maximo= 0;
   private double drawndown=0;
   private double maxima_perdida=0;
   private double gananciames=0;
   private double balanceinicio=10000;
   private int mesespositivo=0;
   private int mesesnegativo=0;
   private int mesactual=0;
   private int diadd;
   private int mesdd;
   private int anodd;
   private int VENTA=2;
   private int COMPRA=1;
   private String strategylabel="NA9";
   String directorio="C:\\temp\\";
   private SimpleDateFormat sdf2;
   private double piv_P;
   private boolean momento=true;
 
   FileWriter fichero;
   BufferedWriter salida;

    //parametrosç
    @Configurable("Instrument") public Instrument strategyInstrument = Instrument.EURUSD;
    @Configurable ("Periodo temporal ")
    public static Period periodo = Period.ONE_HOUR;
    @Configurable("EURUSD") public boolean eu=true;
    @Configurable("GBPUSD") public boolean gu=true;
    @Configurable("AUDUSD") public boolean au=true;
    @Configurable("EURJPY") public boolean ej=true;
    @Configurable("GBPJPY") public boolean gj=true;
    @Configurable("USDCHF") public boolean uc=true;
   // @Configurable("Numero de lotes") public double Lots=0.01;
   public double Lots =0.01;
    public double Slippage=3.0;
    @Configurable (" MM: 1:Fijo 2:Percent ") public int MMType=2;
    @Configurable ("Parametrización :") public boolean parametrizar= false; 
    @Configurable ("Stop loss") public int p_stoploss=1000;
    @Configurable ("Will ") public int p_will=100;
    @Configurable ("Starc ") public int p_starc=25;
    @Configurable ("Atr ") public double p_atr=0;
    @Configurable ("factor ") public int p_takeprofit=500;
    @Configurable ("Posicion ") public double p_posicion=1;

    @Configurable ("dia semana ") public int diade=4;
    @Configurable ("breakeven") public double breakeven= 500;
    @Configurable ("rango de recuperacion") public double rango=1;
    @Configurable ("porcentaje") public double porcentaje=1.02;

    @Configurable ("Debug Mode") public boolean DebugMode=false;
        
    public void onStart(IContext context) throws JFException {
               
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.history = context.getHistory();
        this.console= context.getConsole();
        print("start");
        sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        parametros_iniciales();
        if (parametrizar==true) parametrizacion();
            
    }
    protected void parametrizacion()
    {
        int i=indice(strategyInstrument);
        StopLoss[i]=p_stoploss;
        TakeProfit[i]=p_stoploss*4;
        PeriodoWill[i]=p_will;
        PeriodoStarc[i]=p_starc;
        FactorAtr[i]=p_atr;
        posicion[i]=p_posicion;
        TakeProfit[i]=p_takeprofit;
    }
   
   
    protected void parametros_iniciales()
    {
        
    for(int l=1;l<11;l++)
        {
        piv_R2[l]=100000;
        pivot_revasado[l]=0;
        permite_entrada[l]=true;
        estado_old[l]=1;
        estado_oldv[l]=3;
        arranque[l]=1;
        estado_venta[l]=3;
        estado_compra[l]=1;
        StopLoss[l]=1000;
        TakeProfit[l]=500;
        }     

        //EURUSD
    PeriodoStarc[1]=30;
    FactorAtr[1]=0;
    posicion[1]=2;
    Point[1] = Instrument.EURUSD.getPipValue()/10;
    nombrefichero[1]= strategylabel+"_EURUSD_";
        //GBPUSD
    PeriodoStarc[2]=110;
    posicion[2]=2;
    Point[2] = Instrument.GBPUSD.getPipValue()/10;
    nombrefichero[2]= strategylabel+"_GBPUSD_";
       //AUDUSD
    PeriodoStarc[3]=40;
    posicion[3]=2;
    Point[3] = Instrument.CHFJPY.getPipValue()/10;
    nombrefichero[3]= strategylabel+"_CHFJPY_";
       //EURJPY
    PeriodoStarc[4]=40;
    posicion[4]=2;
    Point[4] = Instrument.EURJPY.getPipValue()/10;
    nombrefichero[4]= strategylabel+"_EURJPY_";
        //GBPJPY
    PeriodoStarc[5]=60;
    posicion[5]=2;
    Point[5] = Instrument.GBPCHF.getPipValue()/10;
        //USDCHF
    PeriodoStarc[6]=45;
    posicion[6]=1;
    Point[6] = Instrument.USDCHF.getPipValue()/10;
    nombrefichero[6]= strategylabel+"_USDCHF_";
        //EURCHF
    PeriodoStarc[7]=45;
    posicion[7]=2;
    Point[7] = Instrument.EURCHF.getPipValue()/10;
    nombrefichero[7]= strategylabel+"_EURCHF_";
        //USDJPY
    PeriodoStarc[8]=25;
    posicion[8]=2;
    Point[8] = Instrument.USDJPY.getPipValue()/10;
    nombrefichero[8]= strategylabel+"_USDJPY_";
        //XAUUSD
    PeriodoStarc[9]=25;
    posicion[9]=2;
    Point[9] = Instrument.XAUUSD.getPipValue()/10;    
    nombrefichero[1]= strategylabel+"_XAUUSD_";
      }  


double precioentrada; // indica el precio de la ultima entrada
double minim, maxim;
//double preu; //es el precio al que se hace la entrada

int registronuevo=0;

int cambio=0;    

// Maquina de estados
// Estado = 1: Busca compra, porque esta en tendencia alcista.
// Estado = 2: compra
// Estado = 3: Busca venta, porque esta en tendencia bajista
// Estado = 4: venta
// Estado = 5: 
// Estado = 6: 
// Estado = 7: 
// Estado = 8: 
// Estado = 9: 



//+----------------------------------------------------            

    public void onStop() throws JFException {
       print ("profit : " + profit_pips);
       print ("profit en Pip: " + profit_ppips);
        print ("Drawndown : "+drawndown+ " año "+ anodd+" mes: "+mesdd+" dia: "+diadd);
        print ("maxima perdida :"+ maxima_perdida);
        //print ("profit/max_loss :"+ profit_pips/maxima_perdida);
        //print ("profit/drawndown :"+ profit_pips/drawndown);
        print ("Profit factor PF :"+ profit_ganadoras/(profit_perdedoras*-1));
        print ("Numero de operaciones:" + num_trades);
        double fiabilidad=(double)ganadoras/(double)num_trades;
        print ("fiabilidad: "+ fiabilidad);
        print ("Payoff: "+ profit_pips/num_trades);
        print ("avr loss:"+ profit_perdedoras/perdedoras);
        print ("avr win:" + profit_ganadoras/ganadoras);
        print ("f optima: "+ (profit_pips*profit_perdedoras*ganadoras)/(num_trades*perdedoras*profit_ganadoras*maxima_perdida));
        print ("meses ganando:"+mesespositivo);
        print ("meses perdiendo:"+mesesnegativo);
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
    
    i=indice(instrument);
    //if(i != 0 && instrument==strategyInstrument){  
    if(i != 0){  
        
    if(DebugMode==true) calendar.setTimeInMillis(tick.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
    if (estado_old[i] != estado_compra[i])
        {
        estado_old[i]=estado_compra[i];
        //print (instrument +"-cambio compra hora"+calendar.get(Calendar.HOUR_OF_DAY)+" dia"+calendar.get(Calendar.DAY_OF_MONTH)+"min "+calendar.get(Calendar.MINUTE)+" Estado compra="+estado_compra[i]+" Precio="+tick.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
        }
    if (estado_oldv[i] != estado_venta[i])
        {
        estado_oldv[i]=estado_venta[i];
        //print (instrument +"-cambio venta hora"+calendar.get(Calendar.HOUR_OF_DAY)+" dia"+calendar.get(Calendar.DAY_OF_MONTH)+"min "+calendar.get(Calendar.MINUTE)+" Estado venta="+estado_venta[i]+" Precio="+tick.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
       // print ("permite entrada="+permite_entrada);    
        }
        
//    calculos(instrument,i,tick);
    if (estado_compra[i] ==1 || estado_compra[i]== 5) // busca compra
         {     
             
         // con el primer tick y en el estado 1 mira los estados de arranque, en el caso de que haya una operacion abierta 
        calculos_tick(instrument,i,tick);
        if (arranque[i]==1)
             estados_arranque(instrument,i,tick);    
        if (estado_compra[i]==1 && corte_do[i]==1){estado_compra[i]=5;}
         }
             // estado 2 Compra  y estado 7 en zona alta (solo control sl y tp) 
    else if (estado_compra[i] == 2 || estado_compra[i] == 7 || estado_compra[i]==9)
         {
         if (positionsTotal(instrument,COMPRA) ==0) {estado_compra[i]=1;}
        // si llega al stop
        else if ((tick.getBid() < stop_compra[i]) && estado_compra[i]!=9)
            {
            IOrder orden_compra= orden(instrument,1);
            if (orden_compra != null)
                {
                //orden_compra.setStopLossPrice(tick.getBid()- Point[i] * StopLoss[i]);
                stop_compra[i]= tick.getBid()- Point[i] * StopLoss[i];
                //orden_compra.setStopLossPrice(tick.getBid()- Point[i] * TakeProfit[i]);
                orden_compra.setTakeProfitPrice(tick.getBid() + Point[i] * TakeProfit[i]*rango);  
                //estado_compra[i]=9;  
             /*   if (orden_compra.getProfitLossInPips() < (-1)*0.25* StopLoss[i])
                    {
                    orden_compra.setStopLossPrice(tick.getBid()- Point[i] * StopLoss[i]);
                    estado_compra[i]=9;                          
                    }*/
                }
            }
         }   
               // estado 3 busca venta   
    if (estado_venta[i] ==3 || estado_venta[i]== 6)
         {           
        calculos_tick(instrument,i,tick);  
         if (corte_up[i]==1){estado_venta[i]=6;}
         } 
             // estado 4 Venta y estado 8 en zona baja (solo control sl y tp)
    else if (estado_venta[i] == 4 || estado_venta[i] == 8 || estado_venta[i]==9)
        {
        if (positionsTotal(instrument,VENTA) == 0) {estado_venta[i]=3;}
        else if ((tick.getBid() > stop_venta[i]) && estado_venta[i]!=9)
            {     
                
            IOrder orden_venta= orden(instrument,2);
            if (orden_venta != null)
                {
                //orden_venta.setStopLossPrice(tick.getBid()+ Point[i] * StopLoss[i]);
                //orden_venta.setStopLossPrice(tick.getBid()+ Point[i] * TakeProfit[i]);
                stop_venta[i]=tick.getBid()+ Point[i] * StopLoss[i];
                orden_venta.setTakeProfitPrice(tick.getBid() - Point[i] * TakeProfit[i]*rango);
               /* if (orden_venta.getProfitLossInPips() < (-1)*0.25* StopLoss[i])
                    {
                    orden_venta.setStopLossPrice(tick.getBid() + Point[i] * StopLoss[i]);
                    estado_venta[i]=9;
                    }*/
                }
            }
        // si llega al stop
            // pone el nuevo profit
            // pone el nuevo stop loss            
        }
// detecta paso por pivot point
    if ((tick.getBid()> piv_R2[i] || tick.getBid()<piv_S2[i]) && diade<=2)
        {
            
        //print("hora de revasamiento de pivot= "+calendar.get(Calendar.HOUR_OF_DAY)+ "y dia de la semana"+calendar.get(Calendar.DAY_OF_WEEK)); 
        pivot_revasado[i]=1;
        permite_entrada[i]=false;    
        }  
    if (instrument == strategyInstrument)
        {
        if (momento== true && equity>(cuenta*porcentaje))
            {
           //print("momento superior a : dia="+calendar.get(Calendar.DAY_OF_MONTH)+"mes="+calendar.get(Calendar.MONTH));    
            momento=false;
            }    
        }          
    }
}
    protected void estados_arranque(Instrument instrument, int i, ITick close) throws JFException
    {
    if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
    if (positionsTotal(instrument,0)>0)
   // busca la tendencia: 
       {
        double preu=history.getBar(instrument,periodo,OfferSide.BID,1).getClose();
       if(positionsTotal(instrument,COMPRA)>=1)
           {
           if (preu> kcup[i]) estado_compra[i]=7;
           else estado_compra[i]=2;        
           }
       if(positionsTotal(instrument,VENTA)>=1)
           {
           if (preu< kcdo[i]) estado_venta[i]=8;
           else estado_venta[i]=4;        
           }
           
       }
       // calcula pivot del dia anterior
    List <IBar> barradia = history.getBars (instrument, Period.ONE_HOUR, OfferSide.BID,Filter.ALL_FLATS,49,
            history.getBarStart(Period.DAILY,close.getTime()),0);  
   double maximo=0;
   double minimo=100000;    
   double pivot_p,pivot_r2,pivot_s2;     
    for (int k=0;k<24;k++)
        {        
        if (barradia.get(k).getHigh()> maximo) maximo =barradia.get(k).getHigh();
        if (barradia.get(k).getLow()< minimo) minimo =barradia.get(k).getLow();
        //print("hora "+k+" maximo="+barradia.get(k).getHigh());    
        }
    pivot_p= (maximo+minimo+barradia.get(24).getClose())/3; 
    pivot_r2= pivot_p+maximo-minimo;
    pivot_s2= pivot_p-maximo+minimo;   
    //print("anterior maximo="+maximo+" y minimo="+minimo+" cierre="+barradia.get(24).getClose()+" el pivot es="+pivot_p);  
    //print("r2="+pivot_r2+" y s2="+pivot_s2);
    maximo=0;
    minimo=100000;
    for (int k=25;k<49;k++)
        {
        if (barradia.get(k).getHigh()> maximo) maximo =barradia.get(k).getHigh();
        if (barradia.get(k).getLow()< minimo) minimo =barradia.get(k).getLow();            
        }  
    //print("actual maximo="+maximo+" y minimo="+minimo+" cierre="+barradia.get(48).getClose()+" el pivot es="+pivot_p); 
    if (maximo>pivot_r2 || minimo<pivot_s2)
        {
        permite_entrada[i]=false;    
        } 
    else
        {
        permite_entrada[i]=true;    
        }    
    //print(instrument+"permite entrada="+permite_entrada[i]);    
    pivot_p= (maximo+minimo+barradia.get(48).getClose())/3; 
    piv_R2[i]= pivot_p+maximo-minimo;
    piv_S2[i]= pivot_p-maximo+minimo;   

    arranque[i]=0; 
    print (instrument +"-inicio "+" Estado compra="+estado_compra[i]+" Estado venta="+estado_venta[i]+" Precio="+close.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));       
      //print (instrument +"-4h "+" Estado="+estado[i]+" Precio="+bidbar.getClose()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
    }
        
    public void calculos_tick(Instrument instrument, int j, ITick close) throws JFException
    {
            
    if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
     double ma = indicators.sma(instrument, periodo, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
   kcup[i]=ma;
   kcdo[i]=ma;
    if (will[j] < -80) tendencia[j]=2;
    else if (will[j] > -20) tendencia[j]=1;
     
    corte_up[j]=0;
    corte_do[j]=0;
    //corte_medio=0;
    if (close.getBid()>kcup[j]) corte_up[j]=1;
    if (close.getBid()<kcdo[j]) corte_do[j]=1;
    }
          
    public void calculos (Instrument instrument, int j, ITick close) throws JFException
    {
    if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));

    //will[j]= indicators.willr(instrument, periodo, OfferSide.BID, PeriodoWill[j],Filter.ALL_FLATS,1,close.getTime(), 0)[0];   
    double ma = indicators.sma(instrument, periodo, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
    //double ma = indicators.sma(instrument, periodo, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.NO_FILTER,1,close.getTime(),0)[0];
    //double sma = indicators.sma(instrument, periodo, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],1);
    //double deviation = indicators.atr(instrument, periodo, OfferSide.BID,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0]*FactorAtr[j];
    kcup[i]=ma;
    kcdo[i]=ma;
    corte_up[j]=0;
    corte_do[j]=0;
    giroup[j]=0;
    girodo[j]=0;
    //corte_medio=0;
    double minbarra, maxbarra;
    double maximo=0;
    double minimo= 100000;

    IBar barra =history.getBar(instrument, periodo, OfferSide.BID,2);
    maximo = barra.getHigh();
    minimo = barra.getLow();    
    elminimo=minimo;
    elmaximo=maximo;
//    if(estado_compra[j]==5)
//        print(instrument+" Superior a:"+round(maximo,5));
//   if(estado_venta[j]==6)
//      print(instrument+" Inferior a:"+round(minimo,5));
      
    elcorte=kcdo[j];
    if (close.getBid()>kcup[j]) corte_up[j]=1;
    if (close.getBid()<kcdo[j]) corte_do[j]=1;
    if (close.getBid()>maximo)
        {
       giroup[j]=1;
       //print("actual "+close.getBid()+" max_ult_vela "+barra.getHigh());
       }
    //if (close.getBid()<barra.getLow())
    if (close.getBid()<minimo)
       girodo[j]=1;   
    }
    
    protected int indice(Instrument instrument)
    {
          if ((instrument == Instrument.EURUSD) && eu==true)
              return 1;
          if ((instrument == Instrument.GBPUSD) && gu==true)
              return 2;
          if ((instrument == Instrument.CHFJPY) && au==true)
              return 3;
          if ((instrument == Instrument.EURJPY) && ej==true)
              return 4;
          if ((instrument == Instrument.GBPCHF) && gj==true)
              return 5;
          if ((instrument == Instrument.USDCHF) && uc==true)
              return 6;
          if ((instrument == Instrument.EURCHF) && uc==true)
              return 7;
          if ((instrument == Instrument.USDJPY) && uc==true)
              return 8;
          if ((instrument == Instrument.XAUUSD) && uc==true)
             return 9;
        //  if ((instrument == Instrument.USDCHF) && uc==true)
        //      return 10;
          else  return 0;                           
    }   
  
  
    protected IOrder orden(Instrument instrument, int tipo) throws JFException {
                  
                  
        String label_estrategia;
        for (IOrder order : engine.getOrders(instrument)) 
            {
            label_estrategia= order.getLabel().substring(0,3);                  
            if ((tipo==1) && order.getOrderCommand()==IEngine.OrderCommand.BUY && (label_estrategia.compareTo(strategylabel)>0))
                return order;
            if ((tipo==2) && order.getOrderCommand()==IEngine.OrderCommand.SELL && (label_estrategia.compareTo(strategylabel)>0))
                return order;  
            } 
        return null;    
     } 

     // count opened positions
    protected int positionsTotal(Instrument instrument, int tipo) throws JFException {
        int counter = 0;
        for (IOrder order : engine.getOrders(instrument)) 
            {
                
            String label_estrategia= order.getLabel().substring(0,3);
            if (order.getState() == IOrder.State.FILLED && (label_estrategia.compareTo(strategylabel)>0)) 
                {
                if (tipo==0)    
                    counter++;
                else if (tipo==1 && order.getOrderCommand()==IEngine.OrderCommand.BUY)
                    counter++;    
                else if (tipo==2 && order.getOrderCommand()==IEngine.OrderCommand.SELL)
                    counter++;    
                }
        }
        return counter;
    }
    private void waitUntilOrderFilled(IOrder order) {
         while (!order.getState().equals(IOrder.State.CANCELED) && !order.getState().equals(IOrder.State.CLOSED)
                         && !order.getState().equals(IOrder.State.FILLED)){
                        order.waitForUpdate(1000);
                }
        // print(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+"--"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+
        //   " -state "+order.getState());       
        }    
 
    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = strategylabel+label.substring(0, 2) + label.substring(3, 5);
        label = label + (tagCounter++);
        label = label.toLowerCase();
        return label;
    }
    
   
    
    protected double arrondirLots(double nbLots) 
    {
    //  LOTSTEO = step for changing lots..
    //double lotStep = MarketInfo(Symbol(), MODE_LOTSTEP);
    double lotStep = 0.001;
    double tempDouble = nbLots + lotStep/2;
    tempDouble /= lotStep;
    int tempInt = (int)tempDouble;
    return (tempInt*lotStep);    
    }
    
    protected double getBalance() throws JFException
    {
    double calc_balance=0;    
    for (IOrder order : engine.getOrders()) 
        {
        if (order.getState() == IOrder.State.OPENED) 
            {
            calc_balance=calc_balance+order.getProfitLossInAccountCurrency();                
            }
         }
     return (calc_balance+equity);     
     }  
          
protected double lots() throws JFException
    {
    double Lots=0.01;
    if(MMType==1)Lots=0.01;
    else if(MMType==2)
        {
        int enterolotes=(int)(equity/1000);    
        Lots=enterolotes*0.0005;   
        //Lots=enterolotes*0.001;   
        if (Lots==0) Lots=0.001; 
        //if (Lots==0) Lots=0.0005; 
        }
//    Lots=arrondirLots(Lots2);
//    print("lotes "+Lots+" y sin redondeo="+ Lots2);
    return Lots;
    }    

    public void onBar(Instrument instrument, Period period, IBar askbar, IBar bidbar) throws JFException {
    
i= indice(instrument);
//if(i != 0  && period==Period.FOUR_HOURS)
if(i != 0  && period==Period.DAILY)
    {
        
    double piv_high,piv_low, piv_close;
   if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
   else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
   int dia =calendar.get(Calendar.DAY_OF_MONTH);
   int mes =calendar.get(Calendar.MONTH);
   int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
    // calcula el pivot point diario
    if (dia_semana >1 && dia_semana <=6)
        {
        if (pivot_revasado[i]==0)
            {
            permite_entrada[i]=true;
            //print("permite entrada dia:"+dia_semana);
            //print("precio="+piv_close);
            }
        else
            {
             if (((bidbar.getClose()> piv_R2[i] || bidbar.getClose()<piv_S2[i]) && (diade==2 || diade==6)) ||
                 (diade==0 || diade==4))
                pivot_revasado[i]=0; 
            else
                {
                pivot_revasado[i]=0;
                permite_entrada[i]=true;    
                }       
            //print("no hay entrada dia:"+dia_semana);
            }
        piv_high = bidbar.getHigh();
        piv_low = bidbar.getLow();  
        piv_close = bidbar.getClose();
        piv_P= (piv_high+piv_low+piv_close)/3;
        piv_R2[i]= piv_P +piv_high-piv_low;
        piv_S2[i]= piv_P -piv_high+piv_low;
        
       // print("mes="+mes+" dia="+dia+"pivots="+piv_P+" y "+piv_R2[i]);     
      // print (instrument+"dia="+dia+" permite entrada="+permite_entrada[i]);   
        }
        if (instrument == strategyInstrument)
        {
            
        //momento=true;
        cuenta = equity;
/*        if (cuenta > (old_cuenta*porcentaje))
            {
            print("dia positivo superior a "+porcentaje+"  : dia="+dia+" mes="+mes+" perdida="+(old_cuenta-cuenta));    
            }   
        else
            {
           if (momento==false)
                    print("dia casi positivo..  a 2% : dia="+dia+" mes="+mes+" perdida="+(old_cuenta-cuenta));        
            }    */
        old_cuenta= cuenta;  
        momento=true;   
        }            
    }

if(i != 0  && period==periodo)
   {    
   if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
   else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
   //if (calendar.get(Calendar.DAY_OF_WEEK)>=2 && calendar.get(Calendar.DAY_OF_WEEK)<=6) 
   int minutos =calendar.get(Calendar.MINUTE);
   int hora =calendar.get(Calendar.HOUR_OF_DAY); 
   int dia =calendar.get(Calendar.DAY_OF_MONTH);
   int mes =calendar.get(Calendar.MONTH);
   int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
   int n = hora - ((int)(hora/4)*4);
   if (abierto(hora,dia_semana)==1) 
   //if (abierto(hora,dia_semana)==1 && (diade==0 || diade==dia_semana)) 
       {
       // detecta paso por pivot point
        if ((bidbar.getClose()> piv_R2[i] || bidbar.getClose()<piv_S2[i]) && diade>=3)
            {      
            //print("hora de revasamiento de pivot= "+calendar.get(Calendar.HOUR_OF_DAY)+ "y dia de la semana"+calendar.get(Calendar.DAY_OF_WEEK)); 
            pivot_revasado[i]=1;
            permite_entrada[i]=false;    
            }
        else if (diade==3) permite_entrada[i]=true;            
                 
       //if(pivot_revasado==1)        print("hora de revasamiento de pivot= "+calendar.get(Calendar.HOUR_OF_DAY)+"pivots="+piv_S2+" y "+piv_R2); 
       //print("los minutitos"+minutos);
       calculos(instrument,i,history.getLastTick(instrument));
       //print ("barra 4 "+dia+" y hora "+hora+" "+elmaximo+" y el minimo "+elminimo+" actual "+bidbar.getClose());    
       // Estado 2 Compra 
       if (estado_compra[i] == 2)
         {
         //if (corte_up[i]==1){estado_compra[i]=7;}
         }
         // Estado 5 compra busca giro
      else if (estado_compra[i]==5)
         {
         if (permite_entrada[i]==false)
             estado_compra[i]=1;    
         
         else if(giroup[i]==1)
             {
              Lots=posicion[i]*lots();
             //IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.BUY, Lots, 0, Slippage,askbar.getClose()
             //           - Point[i] * StopLoss[i], askbar.getClose() + Point[i] * TakeProfit[i]);
             stop_compra[i]= bidbar.getClose()- Point[i] * StopLoss[i];
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.BUY, Lots, 0,Slippage,
                 0,askbar.getClose() + Point[i] * TakeProfit[i]);
             }
          }       
         // Estado 7 para cerrar compra
      else if  (estado_compra[i]==7)
          {
              
          IOrder laorden= orden(instrument,1);
          
          
          //print ("profit="+laorden.getProfitLossInPips()+" y corta sma="+(kcup[i]>bidbar.getClose()));
          //if (kcup[i]>bidbar.getClose() && laorden.getProfitLossInPips()>breakeven)
          if (kcup[i]>bidbar.getClose())
             {
          //OrderClose(i, posicion, Bid, Slippage, MediumSeaGreen);
              //IOrder laorden= engine.getOrders(instrument).get(0);
              //IOrder laorden= orden(instrument,1);
              if(laorden.getOrderCommand() == IEngine.OrderCommand.BUY) 
                 {
                laorden.close();               
                //waitUntilOrderFilled(laorden);
                estado_compra[i]=1;
                }                 
             }
          }        
        // Estado 4 Venta
       if (estado_venta[i] == 4)
         {
         //if(corte_do[i]==1){estado_venta[i]=8;}    
          }   
         // Estado 6 venta busca giro
      else if (estado_venta[i]==6)
         {     
         if (permite_entrada[i]==false)
             estado_venta[i]=3;    
         else if(girodo[i]==1)
             {
             Lots=posicion[i]*lots();
             //IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.SELL, Lots, 0, Slippage,bidbar.getClose()
             //           + Point[i] * StopLoss[i], bidbar.getClose() - Point[i] * TakeProfit[i]);
             stop_venta[i]= bidbar.getClose()+ Point[i] * StopLoss[i];
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.SELL, Lots, 0, Slippage,0
                        , bidbar.getClose() - Point[i] * TakeProfit[i]);
             }
          } 
         // Estado 8 para cerrar venta
      else if (estado_venta[i]==8)
          {
          IOrder laordenv= orden(instrument,2);
          //if (kcdo[i]<bidbar.getClose())
          //if (kcdo[i]<bidbar.getClose() && laordenv.getProfitLossInPips()>breakeven)
          if (kcdo[i]<bidbar.getClose())
             {
         //OrderClose(i, posicion, Ask, Slippage, MediumSeaGreen);
             //IOrder laorden= engine.getOrders(instrument).get(0);
              //IOrder laordenv= orden(instrument,2);
             if(laordenv.getOrderCommand() == IEngine.OrderCommand.SELL) 
                {
                laordenv.close();               
                //waitUntilOrderFilled(laorden);
                estado_venta[i]=3;
                }  
             }                     
         }
     // print (instrument +"-h "+" Estado compra="+estado_compra[i]+" Estado venta="+estado_venta+" Precio="+bidbar.getClose()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
      }    
   }   
}

    public int normaliza_hora( int dia, int dia_sem, int mes)
    // para el cambio de hora: horario de verano diferencia 2 horas respecto mt4 ---> n=1
    // horario de verano se activa ultimo domingo de marzo hasta ultimo domingo de octubre
    // horario de invierno diferencia 1 hora respecto mt4 ---> n=3
    {
    if (mes > 9 || mes < 2) return 3;
    else if (mes < 9 && mes > 2) return 2;    
    else if (mes == 2)
        {
        if ((dia+(8-dia_sem))<= 31) return 3;
        else return 2;    
        }
    else if (mes == 9)
        {
        if ((dia+(8-dia_sem))<= 31) return 2;
        else return 3;    
        }
    return 2;    
    }
    public int abierto(int hora, int semana)
    {
       if (semana<=5 && semana>=2) return 1;
       else if (semana==7) return 0; 
       else if (semana==6) if(hora>=21) return 0; else return 1;
       else if (semana==1)
           if (hora>=22 && diade==0) return 1; else return 0;           
           //if (hora>=22) return 1; else return 0;           
       else return 1;
    }

    public int cierre()
    {
        
       if (calendar.get(calendar.DAY_OF_WEEK)==6 || calendar.get(calendar.DAY_OF_WEEK)==7) return 0; 
       if (calendar.get(calendar.DAY_OF_WEEK)==1)
           if (calendar.get(calendar.HOUR_OF_DAY)==22) return 1; else return 0;           
       else if (calendar.get(Calendar.HOUR_OF_DAY)==21) return 1;
           else return 0;
    }
    public int findesemana(IBar barra)
    {
        return 0;
    }
	
	public void onMessage(IMessage message) throws JFException {
        
        if(message.getType()==IMessage.Type.ORDER_FILL_OK)
            {
                
            
            int im=indice(message.getOrder().getInstrument());
            //print("orden confirmada: "+
        //print("MEN_OK:"+message.getOrder().getLabel()+":"+message);
            //if (estado_venta[im]==6) estado_venta[im]=4;
            if (estado_venta[im]==6 && message.getOrder().getOrderCommand()==IEngine.OrderCommand.SELL) 
                {
                //print("Venta ok");    
                estado_venta[im]=4;
                }
            //if (estado_compra[im]==5) estado_compra[im]=2;
            if (estado_compra[im]==5 && message.getOrder().getOrderCommand()==IEngine.OrderCommand.BUY) 
                {
                //print("compra ok");
                estado_compra[im]=2;
                }
            }
        if(message.getType()==IMessage.Type.ORDER_CLOSE_OK)
            {
            if(DebugMode==true) calendar.setTimeInMillis(history.getLastTick(message.getOrder().getInstrument()).getTime());  // esto solo es necesario  cuando estamos en backtesting..
            else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
            int elmes =calendar.get(Calendar.MONTH);
            int elano = calendar.get(Calendar.YEAR);
            if(mesactual!= elmes)
                {
                print("Año:"+elano+" Mes:"+(mesactual+1)+" Ganancia Mensual="+ gananciames+" Porcentual:"+ (gananciames/balanceinicio)*100);
                if (gananciames>0) mesespositivo++;
                else mesesnegativo++;    
                gananciames=0; 
                mesactual=elmes; 
                //balanceinicio=profit_pips;                
                }
            IOrder orden=message.getOrder();    
            num_trades++;
             double profit_operation= orden.getProfitLossInUSD();
            profit_pips = profit_pips+ profit_operation;
            double profit_poperation= orden.getProfitLossInPips();
            profit_ppips = profit_ppips+ profit_poperation;
           gananciames=gananciames+profit_operation;
            if (profit_operation>=0)    
                {
                ganadoras++;
                profit_ganadoras=profit_ganadoras+profit_operation;    
                 } 
            else
                {
                if(profit_operation<maxima_perdida) maxima_perdida=profit_operation;    
                perdedoras++;
                profit_perdedoras= profit_perdedoras+profit_operation;    
                }     
             if (profit_pips> maximo) maximo = profit_pips;
             if ((maximo-profit_pips)> drawndown) 
                 {
                 drawndown = maximo-profit_pips;   
                 //if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
                 if(DebugMode==true) calendar.setTimeInMillis(history.getLastTick(message.getOrder().getInstrument()).getTime());  // esto solo es necesario  cuando estamos en backtesting..
                 else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
                 diadd =calendar.get(Calendar.DAY_OF_MONTH);
                 mesdd =calendar.get(Calendar.MONTH);
                 anodd =calendar.get(Calendar.YEAR);
                 }
             //print ("maximo :"+maximo+ " equity :"+equity+" drawndown : "+drawndown);    
             int il=indice(message.getOrder().getInstrument());
             String nomfichero= directorio+nombrefichero[il]+(elmes+1)+".csv";
            try
                {
            fichero = new FileWriter(nomfichero, true);
            salida= new BufferedWriter(fichero);
            salida.append(orden.getOrderCommand()+","+orden.getAmount()+","+sdf2.format(orden.getCreationTime())+","+orden.getOpenPrice()+","
                +sdf2.format(orden.getCloseTime())+","+orden.getClosePrice()+","+orden.getProfitLossInPips()+","+orden.getProfitLossInAccountCurrency());
            salida.newLine();    
            salida.close();
                } catch (IOException e)
                {
                    print ("errror");
                }

            } 
    }

    public void onAccount(IAccount account) throws JFException {
        this.equity = account.getEquity();
    }
    public void print(Object o) {

        this.console.getOut().println(o.toString());

    }
    private double round(double value, int decimalPlace)
    {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }


}