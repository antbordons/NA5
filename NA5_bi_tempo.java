                                            
package jforex;
import java.math.*;
//import java.math.*;
import java.util.*;
import java.text.*;

import com.dukascopy.api.*;


public class NA5_bi_tempo implements IStrategy {
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
    private int []estado_compra = new int [7];
    private int []estado_venta = new int [7];
    private int []estado_old = new int [7];
    private int []estado_oldv = new int [7];
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
   private int VENTA=2;
   private int COMPRA=1;
   private String strategylabel="NA8";


    //parametrosç
    @Configurable("Instrument") public Instrument strategyInstrument = Instrument.EURUSD;
    @Configurable ("Periodo temporal ")
    public static Period periodo = Period.ONE_HOUR;
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
    @Configurable ("factor ") public int p_takeprofit=1000;
    @Configurable ("Posicion ") public int p_posicion=1;

    @Configurable ("Debug Mode") public boolean DebugMode=false;
        
    public void onStart(IContext context) throws JFException {
               
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.history = context.getHistory();
        this.console= context.getConsole();
        print("start");
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
        //if (i==2) TakeProfit[i]=p_stoploss*2;
        TakeProfit[i]=p_takeprofit;
    }
   
   
    protected void parametros_iniciales()
    {
        //EURUSD
        estado_old[1]=1;
        estado_oldv[1]=3;
        if(estadoeu==0)
            {
            arranque[1]=1;
            //estado[1]=1;
            estado_venta[1]=3;
            estado_compra[1]=1;
            //cambio 3-agosto-2011
            }
        else
            {
            arranque[1]=0;
            estado_compra[1]=estadoeu;    
            }    
        StopLoss[1]=1000;
        TakeProfit[1]=500;
        PeriodoWill[1]=130;
        PeriodoStarc[1]=50;
        FactorAtr[1]=1.1;
        posicion[1]=2;
        Point[1] = Instrument.EURUSD.getPipValue()/10;
        //GBPUSD
        estado_old[2]=1;
        estado_oldv[2]=3;
        if(estadogu==0)
            {
            arranque[2]=1;
            estado_venta[2]=3;
            estado_compra[2]=1;
            //estado[2]=1;
             }
        else
            {
            arranque[2]=0;
            estado_compra[2]=estadogu;    
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
       //AUDUSD
        estado_old[3]=1;
        estado_oldv[3]=3;
        if(estadoau==0)
            {
            arranque[3]=1;
            estado_venta[3]=3;
            estado_compra[3]=1;
            //estado[3]=1;
            }
        else
            {
            arranque[3]=0;
            estado_compra[3]=estadoau;    
            }    
        StopLoss[3]=1000;
        TakeProfit[3]=4000;
        PeriodoWill[3]=140;
        PeriodoStarc[3]=25;
        FactorAtr[3]=1.1;
         posicion[3]=2;
        Point[3] = Instrument.AUDUSD.getPipValue()/10;
       //EURJPY
        estado_old[4]=1;
        estado_oldv[4]=3;
        if(estadoej==0)
            {
            arranque[4]=1;
            estado_venta[4]=3;
            estado_compra[4]=1;
            //estado[4]=1;
            }
        else
            {
            arranque[4]=0;
            estado_compra[4]=estadoej;    
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
        //GBPJPY
        estado_old[5]=1;
        estado_oldv[5]=3;
        if(estadogj==0)
            {
            arranque[5]=1;
            estado_venta[5]=3;
            estado_compra[5]=1;
            //estado[5]=1;
            }
        else
            {
            arranque[5]=0;
            estado_compra[5]=estadogj;    
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
        //USDCHF
        estado_old[6]=1;
        estado_oldv[6]=3;
        if(estadouc==0)
            {
            arranque[6]=1;
            estado_venta[6]=3;
            estado_compra[6]=1;
            //estado[6]=1;
            }
        else
            {
            arranque[6]=0;
            estado_compra[6]=estadouc;    
            }    
        StopLoss[6]=1000;
        TakeProfit[6]=2000;
        PeriodoWill[6]=140;
        PeriodoStarc[6]=25;
        FactorAtr[6]=1.5;
        posicion[6]=2;
        Point[6] = Instrument.USDCHF.getPipValue()/10;
        
/*        for(int l=1;l<7;l++)
        {
            if(l!=2 && l!=6) TakeProfit[l]=StopLoss[l]*4;
            else TakeProfit[l]=StopLoss[l]*2;   
        }     */
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
    if (estado_old[i] != estado_compra[i])
        {
        estado_old[i]=estado_compra[i];
        print (instrument +"-cambio compra"+" Estado compra="+estado_compra[i]+" Precio="+tick.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
        }
    if (estado_oldv[i] != estado_venta[i])
        {
        estado_oldv[i]=estado_venta[i];
        print (instrument +"-cambio venta"+" Estado venta="+estado_venta[i]+" Precio="+tick.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
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
    else if (estado_compra[i] == 2 || estado_compra[i] == 7)
         {
         if (positionsTotal(instrument,COMPRA) ==0) {estado_compra[i]=1;}
         }   
               // estado 3 busca venta   
    if (estado_venta[i] ==3 || estado_venta[i]== 6)
         {           
        calculos_tick(instrument,i,tick);  
         if (corte_up[i]==1){estado_venta[i]=6;}
         } 
             // estado 4 Venta y estado 8 en zona baja (solo control sl y tp)
    else if (estado_venta[i] == 4 || estado_venta[i] == 8)
        {
        if (positionsTotal(instrument,VENTA) == 0) {estado_venta[i]=3;}
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
    arranque[i]=0; 
    print (instrument +"-inicio "+" Estado compra="+estado_compra[i]+" Estado venta="+estado_venta[i]+" Precio="+close.getBid()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));       
      //print (instrument +"-4h "+" Estado="+estado[i]+" Precio="+bidbar.getClose()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
    }
        
    public void calculos_tick(Instrument instrument, int j, ITick close) throws JFException
    {
            
    if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));

     will[j]= indicators.willr(instrument, periodo, OfferSide.BID, PeriodoWill[j],Filter.ALL_FLATS,1,close.getTime(), 0)[0];   
     double ma = indicators.sma(instrument, periodo, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
    double deviation = indicators.atr(instrument, periodo, OfferSide.BID,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0]*FactorAtr[j];
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

    will[j]= indicators.willr(instrument, periodo, OfferSide.BID, PeriodoWill[j],Filter.ALL_FLATS,1,close.getTime(), 0)[0];   
    double ma = indicators.sma(instrument, periodo, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
    double deviation = indicators.atr(instrument, periodo, OfferSide.BID,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0]*FactorAtr[j];
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

    IBar barra =history.getBar(instrument, periodo, OfferSide.BID,2);
    maximo = barra.getHigh();
    minimo = barra.getLow();    
   /* List <IBar> barra = history.getBars(instrument, Period.ONE_HOUR, OfferSide.BID, Filter.ALL_FLATS, 8,history.getBarStart(Period.ONE_HOUR,close.getTime()),0);
    for(int k=0;k<1;k++)
           {
           maxbarra = barra.get(0).getHigh();
           minbarra = barra.get(0).getLow();    
           if (maximo < maxbarra) maximo=maxbarra;   
           if (minimo > minbarra) minimo=minbarra;
           }*/
    elminimo=minimo;
    elmaximo=maximo;
    if(estado_compra[j]==5)
        print(instrument+" Superior a:"+round(maximo,5));
    if(estado_venta[j]==6)
        print(instrument+" Inferior a:"+round(minimo,5));
      
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
  

     // count opened positions
    protected int positionsTotal(Instrument instrument, int tipo) throws JFException {
        int counter = 0;
        for (IOrder order : engine.getOrders(instrument)) 
            {
            if (order.getState() == IOrder.State.FILLED) 
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
        Lots=enterolotes*0.001;   
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

if(i != 0  && period==periodo)
   {    
   //if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
   if(DebugMode==true) calendar.setTimeInMillis(history.getLastTick(instrument).getTime());  // esto solo es necesario  cuando estamos en backtesting..
   else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
   //print ("will "+will[i]+" instrum "+ i+" y hora"+calendar.get(Calendar.HOUR_OF_DAY)+" y "+calendar.get(Calendar.DAY_OF_MONTH));
    //print ("will "+will[i]);
    //print ("tendencia "+tendencia[i]);
   //if (calendar.get(Calendar.DAY_OF_WEEK)>=2 && calendar.get(Calendar.DAY_OF_WEEK)<=6) 
   int minutos =calendar.get(Calendar.MINUTE);
   int hora =calendar.get(Calendar.HOUR_OF_DAY); 
   int dia =calendar.get(Calendar.DAY_OF_MONTH);
   int mes =calendar.get(Calendar.MONTH);
   int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
   int n = hora - ((int)(hora/4)*4);
   if (minutos>=58) n=n+1;
   //if (minutos<=1) n=n-1;
   //if (n == normaliza_hora(dia,dia_semana,mes)) 
   if (abierto(hora,dia_semana)==1) 
       {   
       //print("los minutitos"+minutos);
       calculos(instrument,i,history.getLastTick(instrument));
       //print ("barra 4 "+dia+" y hora "+hora+" "+elmaximo+" y el minimo "+elminimo+" actual "+bidbar.getClose());    
       // Estado 2 Compra 
       if (estado_compra[i] == 2)
         {
         if (corte_up[i]==1){estado_compra[i]=7;}
         }
         // Estado 5 compra busca giro
      else if (estado_compra[i]==5)
         {
         if(giroup[i]==1)
             {
              Lots=posicion[i]*lots();
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.BUY, Lots, 0, Slippage,askbar.getClose()
                        - Point[i] * StopLoss[i], askbar.getClose() + Point[i] * TakeProfit[i]);
             }
          }       
         // Estado 7 para cerrar compra
      else if  (estado_compra[i]==7)
          {
          if (kcup[i]>bidbar.getClose())
             {
          //OrderClose(i, posicion, Bid, Slippage, MediumSeaGreen);
              IOrder laorden= engine.getOrders(instrument).get(0);
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
         if(corte_do[i]==1){estado_venta[i]=8;}    
          }   
         // Estado 6 venta busca giro
      else if (estado_venta[i]==6)
         {     
         if(girodo[i]==1)
             {
             Lots=posicion[i]*lots();
         //i=apertura_orden("", OP_SELL,posicion,1,StopLoss, TakeProfit);
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.SELL, Lots, 0, Slippage,bidbar.getClose()
                        + Point[i] * StopLoss[i], bidbar.getClose() - Point[i] * TakeProfit[i]);
             }
          } 
         // Estado 8 para cerrar venta
      else if (estado_venta[i]==8)
          {
          if (kcdo[i]<bidbar.getClose())
             {
         //OrderClose(i, posicion, Ask, Slippage, MediumSeaGreen);
             IOrder laorden= engine.getOrders(instrument).get(0);
             if(laorden.getOrderCommand() == IEngine.OrderCommand.SELL) 
                {
                laorden.close();               
                //waitUntilOrderFilled(laorden);
                estado_venta[i]=3;
                }  
             }                     
         }
      print (instrument +"-h "+" Estado compra="+estado_compra[i]+" Estado venta="+estado_venta+" Precio="+bidbar.getClose()+" Banda Up="+round(kcup[i],5)+" Banda Down="+round(kcdo[i],5));
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
            if (estado_venta[im]==6) estado_venta[im]=4;
            if (estado_compra[im]==5) estado_compra[im]=2;
                
            }
        if(message.getType()==IMessage.Type.ORDER_CLOSE_OK)
            {
            if(DebugMode==true) calendar.setTimeInMillis(history.getLastTick(strategyInstrument).getTime());  // esto solo es necesario  cuando estamos en backtesting..
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
                 //if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
                 if(DebugMode==true) calendar.setTimeInMillis(history.getLastTick(strategyInstrument).getTime());  // esto solo es necesario  cuando estamos en backtesting..
                 else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
                 diadd =calendar.get(Calendar.DAY_OF_MONTH);
                 mesdd =calendar.get(Calendar.MONTH);
                 anodd =calendar.get(Calendar.YEAR);
                 }
             //print ("maximo :"+maximo+ " equity :"+equity+" drawndown : "+drawndown);    
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