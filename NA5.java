                                            
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


public class NA5 implements IStrategy {
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
    //private double Lots=0.01;
    private int []estado = new int [7];
    private int []estado_old = new int [7];
    private int []tendencia = new int [7];
    private int []corte_up = new int [7];
    private int []corte_do = new int [7];
    private int []giroup =new int[7];
    private int []girodo = new int[7];
    private int []instrumento_activo= new int [7];
    private double []will = new double [7];
    private double []kcup = new double[7];
    private double []kcdo = new double[7]; 
    private int []arranque = new int[7];   
    private String []nombrefichero = new String[11]; 
    
    // parametros
    private int []PeriodoWill = new int [7];
    private int []PeriodoStarc = new int[7];
    private double []FactorAtr = new double[7];
    private int []StopLoss = new int[7];
    private int []TakeProfit = new int[7];
    private int []posicion = new int[7];
   private double []Point = new double[7];
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

   private String strategylabel="NA5";
/*   String directorio="C:\\temp\\";
   private SimpleDateFormat sdf2;

   FileWriter fichero;
   BufferedWriter salida;*/

    //parametrosç
    @Configurable("Instrument") public Instrument strategyInstrument = Instrument.EURUSD;
    @Configurable("estado eurusd") public int estadoeu=0;
    @Configurable("estado gbpusd") public int estadogu=0;
    @Configurable("estado audusd") public int estadoau=0;
    @Configurable("estado eurjpy") public int estadoej=0;
    @Configurable("estado gbpjpy") public int estadogj=0;
    @Configurable("estado usdchf") public int estadouc=0;
    @Configurable("EURUSD") public boolean eu=true;
    @Configurable("GBPUSD") public boolean gu=true;
    @Configurable("AUDUSD") public boolean au=true;
    @Configurable("EURJPY") public boolean ej=true;
    @Configurable("GBPJPY") public boolean gj=true;
    @Configurable("USDCHF") public boolean uc=true;
   // @Configurable("Numero de lotes") public double Lots=0.01;
   public double Lots =0.01;
  // @Configurable("Spread maximo") public double max_spread= 2.0;
    //@Configurable("Slippage") public double Slippage=3.0;
    public double Slippage=3.0;
    @Configurable (" MM: 1:Fijo 2:Percent ") public int MMType=2;
    @Configurable ("Parametrización :") public boolean parametrizar= false; 
    @Configurable ("Stop loss") public int p_stoploss=800;
    @Configurable ("Will ") public int p_will=100;
    @Configurable ("Starc ") public int p_starc=25;
    @Configurable ("Atr ") public double p_atr=1;
    @Configurable ("factor ") public int factor=4;
    @Configurable ("Posicion ") public int p_posicion=1;

    @Configurable ("Debug Mode") public boolean DebugMode=false;
        
    public void onStart(IContext context) throws JFException {
               
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.history = context.getHistory();
        this.console= context.getConsole();
        print("start");
        //sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        if (i==2) TakeProfit[i]=p_stoploss*2;
        TakeProfit[i]=p_stoploss*factor;
    }
   
   
    protected void parametros_iniciales()
    {
        //EURUSD
        estado_old[1]=1;
        if(estadoeu==0)
            {
            arranque[1]=1;
            estado[1]=1;
            estado[1]=0; //cambio 3-agosto-2011
            }
        else
            {
            arranque[1]=0;
            estado[1]=estadoeu;    
            }    
        StopLoss[1]=900;
        StopLoss[1]=500; // cambio 12-jun-2011
        TakeProfit[1]=3600;
        TakeProfit[1]=3500;// cambio 12-jun-2011
        PeriodoWill[1]=130;
        PeriodoStarc[1]=25;
        FactorAtr[1]=1.2;
        FactorAtr[1]=0.5; // cambio 12-jun-2011
        posicion[1]=2;
        posicion[1]=3; // cambio 12-jun-2011
        Point[1] = Instrument.EURUSD.getPipValue()/10;
        nombrefichero[1]= strategylabel+"_EURUSD_";
        //GBPUSD
        estado_old[2]=1;
        if(estadogu==0)
            {
            arranque[2]=1;
            estado[2]=1;
             }
        else
            {
            arranque[2]=0;
            estado[2]=estadogu;    
            }    
        StopLoss[2]=800;
        TakeProfit[2]=1600;
        TakeProfit[2]=800; // cambio  12-jun-2011
        PeriodoWill[2]=100;
        PeriodoStarc[2]=30;
        PeriodoStarc[2]=65; // cambio 12-jun-2011        
        FactorAtr[2]=1.7;
        FactorAtr[2]=1.3; // cambio 12-jun-2011
         posicion[2]=2;
        Point[2] = Instrument.GBPUSD.getPipValue()/10;
        nombrefichero[1]= strategylabel+"_GBPUSD_";
       //AUDUSD
        estado_old[3]=1;
        if(estadoau==0)
            {
            arranque[3]=1;
            estado[3]=1;
            }
        else
            {
            arranque[3]=0;
            estado[3]=estadoau;    
            }    
        StopLoss[3]=1000;
        TakeProfit[3]=4000;
        PeriodoWill[3]=140;
        PeriodoStarc[3]=25;
        FactorAtr[3]=1.1;
         posicion[3]=2;
        Point[3] = Instrument.AUDUSD.getPipValue()/10;
        nombrefichero[1]= strategylabel+"_AUDUSD_";
       //EURJPY
        estado_old[4]=1;
        if(estadoej==0)
            {
            arranque[4]=1;
            estado[4]=1;
            }
        else
            {
            arranque[4]=0;
            estado[4]=estadoej;    
            }    
        StopLoss[4]=900;
        StopLoss[4]=1000; // cambio 12-jun-2011
        TakeProfit[4]=3600;
        TakeProfit[4]=1600; // cambio 12-jun-2011
        PeriodoWill[4]=100;
        PeriodoStarc[4]=15;
        PeriodoStarc[4]=70; // cambio 12-jun-2011
        FactorAtr[4]=0.9;
        FactorAtr[4]=1.4; // cambio 12-jun-2011
        posicion[4]=2;
        Point[4] = Instrument.EURJPY.getPipValue()/10;
        nombrefichero[1]= strategylabel+"_EURJPY_";
        //GBPJPY
        estado_old[5]=1;
        if(estadogj==0)
            {
            arranque[5]=1;
            estado[5]=1;
            }
        else
            {
            arranque[5]=0;
            estado[5]=estadogj;    
            }    
        StopLoss[5]=2200;
        StopLoss[5]=2000; // cambio 12-jun-2011
        TakeProfit[5]=8500;
        TakeProfit[5]=5500; // cambio 12-jun-2011
        PeriodoWill[5]=1600;
        PeriodoStarc[5]=15;
        PeriodoStarc[5]=75; // cambio 12-jun-2011
        FactorAtr[5]=0.9;
        FactorAtr[5]=0.7;// cambio 12 jun-2011
        posicion[5]=1;
        Point[5] = Instrument.GBPJPY.getPipValue()/10;
        nombrefichero[1]= strategylabel+"_GBPJPY_";
        //USDCHF
        estado_old[6]=1;
        if(estadouc==0)
            {
            arranque[6]=1;
            estado[6]=1;
            }
        else
            {
            arranque[6]=0;
            estado[6]=estadouc;    
            }    
        StopLoss[6]=1000;
        TakeProfit[6]=2000;
        PeriodoWill[6]=140;
        PeriodoStarc[6]=25;
        FactorAtr[6]=1.5;
        posicion[6]=2;
        Point[6] = Instrument.USDCHF.getPipValue()/10;
        nombrefichero[1]= strategylabel+"_USDCHF_";
        
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
        print ("profit en Pip: " + profit_pips);
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
        //print ("f optima: "+ (profit_pips*profit_perdedoras*ganadoras)/(num_trades*perdedoras*profit_ganadoras*maxima_perdida));
        //print ("meses ganando:"+mesespositivo);
        //print ("meses perdiendo:"+mesesnegativo);
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
    
    i=indice(instrument);
    //if(i != 0 && instrument==strategyInstrument){  
    if(i != 0){  
        
    if(DebugMode==true) calendar.setTimeInMillis(tick.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
    if (estado_old[i] != estado[i])
        {
        estado_old[i]=estado[i];
        //print (instrument +"-cambio "+calendar.get(Calendar.DAY_OF_MONTH)+"/"+(calendar.get(Calendar.MONTH)+1)+"/"+calendar.get(Calendar.YEAR)+
        // "-"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+" Estado="+estado[i]+" Precio="+tick.getBid());      //if (giroup[i]==1) print ("giro arriba");    
        print (strategylabel+":"+instrument +"-cambio "+" Estado="+estado[i]+" Precio="+tick.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
        }
//    calculos(instrument,i,tick);
    if (estado[i] ==1 || estado[i]== 5) // busca compra
         {     
             
         // con el primer tick y en el estado 1 mira los estados de arranque, en el caso de que haya una operacion abierta 
        calculos_tick(instrument,i,tick);
        if (arranque[i]==1)
             estados_arranque(instrument,i,tick);    
         if ((estado[i]==1 || estado[i]==5) && tendencia[i]==2){estado[i]=3;}
         else  if (estado[i]==1 && corte_do[i]==1){estado[i]=5;}
         }
               // estado 3 busca venta   
    else if (estado[i] ==3 || estado[i]== 6)
         {           
        calculos_tick(instrument,i,tick);
         if (tendencia[i]==1){estado[i]=1;}   
         else if (corte_up[i]==1){estado[i]=6;}
         } 
             // estado 2 Compra  y estado 7 en zona alta (solo control sl y tp) 
    else if (estado[i] == 2 || estado[i] == 7)
         {
         if (positionsTotal(instrument) ==0) {estado[i]=1;}
         }   
             // estado 4 Venta y estado 8 en zona baja (solo control sl y tp)
    else if (estado[i] == 4 || estado[i] == 8)
        {
        if (positionsTotal(instrument) == 0) {estado[i]=3;}
        }
    }
}
    protected void estados_arranque(Instrument instrument, int i, ITick close) throws JFException
    {
    if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
    if (positionsTotal(instrument)<=0)
   // busca la tendencia: 
       {
       int j=0, n=1;   
       double will=-50;  
       do{               
            n++;
           will= indicators.willr(instrument, Period.FOUR_HOURS, OfferSide.BID, PeriodoWill[i],Filter.ALL_FLATS,n,close.getTime(), 0)[0];   
          }  while ((will < -20 && will> -80) && n<1600); 
        if (will< -80) tendencia[i]=2;
        }
    // si hay una orden busca  estado
    else
       {
       int hora =calendar.get(Calendar.HOUR_OF_DAY); 
       int dia =calendar.get(Calendar.DAY_OF_MONTH);
       int mes =calendar.get(Calendar.MONTH);
       int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
       int normal= normaliza_hora(dia,dia_semana,mes);
       int n = hora - ((int)(hora/4)*4);
       int resultat= n-normal;
       if (resultat<0) resultat=resultat+4;
       double preu=history.getBar(instrument,Period.ONE_HOUR,OfferSide.BID,resultat).getClose();
       if(orden(instrument,1)!=null)
           {
           if (preu> kcup[i]) estado[i]=7;
           else estado[i]=2;    
           }
       else if(orden(instrument,2)!=null)
           {
           if (preu< kcdo[i]) estado[i]=8;
           else estado[i]=4;
           }  
       }
    arranque[i]=0; 
    print (strategylabel+":"+instrument +"-inicio "+" Estado="+estado[i]+" Precio="+close.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));       
      //print (instrument +"-4h "+" Estado="+estado[i]+" Precio="+bidbar.getClose()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
    }
        
    public void calculos_tick(Instrument instrument, int j, ITick close) throws JFException
    {
            
    if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));

     will[j]= indicators.willr(instrument, Period.FOUR_HOURS, OfferSide.BID, PeriodoWill[j],Filter.ALL_FLATS,1,close.getTime(), 0)[0];   
     double ma = indicators.sma(instrument, Period.FOUR_HOURS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
    double deviation = indicators.atr(instrument, Period.FOUR_HOURS, OfferSide.BID,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0]*FactorAtr[j];
   kcup[i]=ma+deviation;
   kcdo[i]=ma-deviation;
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

    will[j]= indicators.willr(instrument, Period.FOUR_HOURS, OfferSide.BID, PeriodoWill[j],Filter.ALL_FLATS,1,close.getTime(), 0)[0];   
    double ma = indicators.sma(instrument, Period.FOUR_HOURS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
    double deviation = indicators.atr(instrument, Period.FOUR_HOURS, OfferSide.BID,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0]*FactorAtr[j];
    kcup[i]=ma+deviation;
    kcdo[i]=ma-deviation;
    if (will[j] < -80) tendencia[j]=2;
    else if (will[j] > -20) tendencia[j]=1;
     
    corte_up[j]=0;
    corte_do[j]=0;
    giroup[j]=0;
    girodo[j]=0;
    //corte_medio=0;
    double minbarra, maxbarra;
    double maximo=0;
    double minimo= 100000;

    List <IBar> barra = history.getBars(instrument, Period.ONE_HOUR, OfferSide.BID, Filter.ALL_FLATS, 8,history.getBarStart(Period.ONE_HOUR,close.getTime()),0);
    for(int k=0;k<4;k++)
           {
           maxbarra = barra.get(k).getHigh();
           minbarra = barra.get(k).getLow();    
           if (maximo < maxbarra) maximo=maxbarra;   
           if (minimo > minbarra) minimo=minbarra;
           }
    elminimo=minimo;
    elmaximo=maximo;
    if(estado[j]==5)
        print(strategylabel+":"+instrument+" Superior a:"+round(maximo,5));
    if(estado[j]==6)
        print(strategylabel+":"+instrument+" Inferior a:"+round(minimo,5));
      
    elcorte=kcdo[j];
    if (close.getBid()>kcup[j]) corte_up[j]=1;
    if (close.getBid()<kcdo[j]) corte_do[j]=1;
    if (close.getBid()>maximo)
        {
       giroup[j]=1;
       }
    if (close.getBid()<minimo)
       girodo[j]=1;   
    }
    
    protected int indice(Instrument instrument)
    {
          if ((instrument == Instrument.EURUSD) && eu==true)
              return 1;
          if ((instrument == Instrument.GBPUSD) && gu==true)
              return 2;
          if ((instrument == Instrument.AUDUSD) && au==true)
              return 3;
          if ((instrument == Instrument.EURJPY) && ej==true)
              return 4;
          if ((instrument == Instrument.GBPJPY) && gj==true)
              return 5;
          if ((instrument == Instrument.USDCHF) && uc==true)
              return 6;
          else  return 0;                           
    } 
      
    protected IOrder orden(Instrument instrument, int tipo) throws JFException {
                  
                  
        String label_estrategia;
        for (IOrder order : engine.getOrders(instrument)) 
            {
            label_estrategia= order.getLabel().substring(0,3);                  
            if ((tipo==1) && order.getOrderCommand()==IEngine.OrderCommand.BUY && (label_estrategia.compareTo(strategylabel)==0))
                return order;
            if ((tipo==2) && order.getOrderCommand()==IEngine.OrderCommand.SELL && (label_estrategia.compareTo(strategylabel)==0))
                return order;  
            } 
        return null;    
     } 
  

     // count opened positions
    protected int positionsTotal(Instrument instrument) throws JFException {
        int counter = 0;
        for (IOrder order : engine.getOrders(instrument)) {
            String label_estrategia= order.getLabel().substring(0,3);
            if (order.getState() == IOrder.State.FILLED && (label_estrategia.compareTo(strategylabel)==0)) 
            {
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
        label = label.substring(0, 2) + label.substring(3, 5);
        label = label + (tagCounter++);
        label = strategylabel+ label.toLowerCase();
        return label;
    }
                     
protected double lots() throws JFException
    {
    double Lots=0.01;
    if(MMType==1)Lots=0.01;
    else if(MMType==2)
        {
        int enterolotes=(int)(equity/1000);    
        Lots=enterolotes*0.001;   
        //Lots=enterolotes*0.0005; // bajamos el riesgo a la mitad  
        if (Lots==0) Lots=0.001; 
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
   if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
   else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
   int dia =calendar.get(Calendar.DAY_OF_MONTH);
   int mes =calendar.get(Calendar.MONTH);
   int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
   //print ("mes"+mes+" dia "+dia+" "+dia_semana);
  // if (normaliza_hora(dia,dia_semana,mes) ==1)
   //    print ("verano");
   //else print ("invierno");            
    }

if(i != 0  && period==Period.ONE_HOUR)
   {    
   //if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
   if(DebugMode==true) calendar.setTimeInMillis(history.getLastTick(instrument).getTime());  // esto solo es necesario  cuando estamos en backtesting..
   else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
   int minutos =calendar.get(Calendar.MINUTE);
   int hora =calendar.get(Calendar.HOUR_OF_DAY); 
   int dia =calendar.get(Calendar.DAY_OF_MONTH);
   int mes =calendar.get(Calendar.MONTH);
   int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
   int n = hora - ((int)(hora/4)*4);
   if (minutos>=58) n=n+1;
   if (n == normaliza_hora(dia,dia_semana,mes) && abierto(hora,dia_semana)==1) 
       {   
       calculos(instrument,i,history.getLastTick(instrument));
       // Estado 2 Compra 
       if (estado[i] == 2)
         {
         if (corte_up[i]==1){estado[i]=7;}
         }
        // Estado 4 Venta
       else if (estado[i] == 4)
         {
         if(corte_do[i]==1){estado[i]=8;}    
          }   
         // Estado 5 compra busca giro
      else if (estado[i]==5)
         {
         if(giroup[i]==1 && will[i]>=-80)
             {
              Lots=posicion[i]*lots();
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.BUY, Lots, 0, Slippage,askbar.getClose()
                        - Point[i] * StopLoss[i], askbar.getClose() + Point[i] * TakeProfit[i]);
             //waitUntilOrderFilled(order);
             }
          }       
         // Estado 6 venta busca giro
      else if (estado[i]==6)
         {     
         if(girodo[i]==1 && will[i]<=-20)
             {
             Lots=posicion[i]*lots();
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.SELL, Lots, 0, Slippage,bidbar.getClose()
                        + Point[i] * StopLoss[i], bidbar.getClose() - Point[i] * TakeProfit[i]);
             //waitUntilOrderFilled(order);
             }
          } 
         // Estado 7 para cerrar compra
      else if  (estado[i]==7)
          {
          if (kcup[i]>bidbar.getClose())
             {
              IOrder laorden = orden(instrument,1);
              if (laorden!=null)
                 {
                laorden.close();               
                //waitUntilOrderFilled(laorden);
                estado[i]=1;
                }                 
             }
          }        
         // Estado 8 para cerrar venta
      else if (estado[i]==8)
          {
          if (kcdo[i]<bidbar.getClose())
             {
              IOrder laorden = orden(instrument,2);
              if (laorden!=null)
                {
                laorden.close();               
                //waitUntilOrderFilled(laorden);
                estado[i]=3;
                }  
             }                     
         }
      print (strategylabel+":"+instrument +"-4h "+" Estado="+estado[i]+" Precio="+bidbar.getClose()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
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
           if (hora>=22) return 1; else return 0;           
       else return 1;
    }

	public void onMessage(IMessage message) throws JFException {
        
        if(message.getType()==IMessage.Type.ORDER_FILL_OK)
            {
            String label_estrategia= message.getOrder().getLabel().substring(0,3);
            if (label_estrategia.compareTo(strategylabel)==0) 
                {
                int im=indice(message.getOrder().getInstrument());
                if (estado[im]==6 && message.getOrder().getOrderCommand()==IEngine.OrderCommand.SELL) 
                    {
                    estado[im]=4;
                    }
                if (estado[im]==5 && message.getOrder().getOrderCommand()==IEngine.OrderCommand.BUY) 
                    {
                    estado[im]=2;
                    }
                }
            }
        if(message.getType()==IMessage.Type.ORDER_CLOSE_OK)
          {
          String label_estrategia2= message.getOrder().getLabel().substring(0,3);
          if (label_estrategia2.compareTo(strategylabel)==0) 
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
                 diadd =calendar.get(Calendar.DAY_OF_MONTH);
                 mesdd =calendar.get(Calendar.MONTH);
                 anodd =calendar.get(Calendar.YEAR);
                 }
             //print ("maximo :"+maximo+ " equity :"+equity+" drawndown : "+drawndown);  
/*             int il=indice(message.getOrder().getInstrument());
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

  */
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