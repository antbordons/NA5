
package jforex;
import java.math.*;
//import java.math.*;
import java.util.*;
import java.text.*;

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
    /*private boolean SignalBuy=false;
    private boolean SignalSell=false;
    private double highrange, lowrange;
    private double trend, trend1, signaltrend;
    private double old_tickbid=0;
    private double old_tickask=0;
    private double old_spread;
    private int MaxTrades=0;
    private int stoplevel=0;
    private int FlagPartial=0;
    private int shift=1;
    private int OrdersThisBar;
    private int orders;
    private int losses;
    private int nbDecimales;
    private int i,dia;
    private Period strategyPeriod = Period.DAILY;*/
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
    
    // parametros
    private int []PeriodoWill = new int [7];
    private int []PeriodoStarc = new int[7];
    private double []FactorAtr = new double[7];
    private int []StopLoss = new int[7];
    private int []TakeProfit = new int[7];
    private int i;
    private double preu;
    //private double Slippage=3;
    private double Point;
    private double contador=0;
    private int findia=0;
    private double will2=0;
   // private int estado_old=1;

    //parametrosç
    @Configurable("Instrument") public Instrument strategyInstrument = Instrument.EURUSD;
    @Configurable("Numero de lotes") public double Lots=0.01;
   @Configurable("Spread maximo") public double max_spread= 2.0;
    @Configurable("Slippage") public double Slippage=3.0;

    @Configurable ("Debug Mode") public boolean DebugMode=false;
        
    public void onStart(IContext context) throws JFException {
               
        this.context = context;
        engine = context.getEngine();
        indicators = context.getIndicators();
        this.history = context.getHistory();
        this.console= context.getConsole();
        print("start");
        Point = strategyInstrument.getPipValue()/10;
        parametros_iniciales();
        //strategyPeriod = Period.DAILY;
        //OrdersThisBar=MaxTradesPerBar;
        //dia=0;
    }
   
    protected void parametros_iniciales()
    {
        //EURUSD
        estado[1]=1;
        StopLoss[1]=500;
        TakeProfit[1]=3200;
        PeriodoWill[1]=150;
        PeriodoStarc[1]=30;
        FactorAtr[1]=0.5;
        //GBPUSD
        estado[2]=1;
        StopLoss[2]=800;
        TakeProfit[2]=1600;
        PeriodoWill[2]=100;
        PeriodoStarc[2]=25;
        FactorAtr[2]=1.9;
        //AUDUSD
        estado[3]=1;
        StopLoss[3]=1000;
        TakeProfit[3]=3900;
        PeriodoWill[3]=140;
        PeriodoStarc[3]=25;
        FactorAtr[3]=1.3;
        //EURJPY
        estado[4]=1;
        StopLoss[4]=900;
        TakeProfit[4]=3500;
        PeriodoWill[4]=100;
        PeriodoStarc[4]=15;
        FactorAtr[4]=1;
        //GBPJPY
        estado[5]=1;
        StopLoss[5]=2200;
        TakeProfit[5]=8500;
        PeriodoWill[5]=1600;
        PeriodoStarc[5]=100;
        FactorAtr[5]=0.8;
        //USDCHF
        estado[6]=1;
        StopLoss[6]=1000;
        TakeProfit[6]=2000;
        PeriodoWill[6]=140;
        PeriodoStarc[6]=25;
        FactorAtr[6]=1.4;
             
      }  
/* variables   */
//extern int StopLoss=2000;  //Stop loss 200PIPs
//extern int TakeProfit= 3000; // Take profit 300PIPs
//extern int tiempo=1440;    // Velas de 1 dia
//extern int periodos_EMA=200;  // Media exponencial de 200 dias
//extern int periodo_starc=5;
//extern int periodo_atr=5;
//extern double val_atr=1.3;

// Variables globales a todos los sistemas
//


//datetime oldTime;
//int tendencia=1;
//int zona=0;
//int estado= 1; // es el estado de la maquina de estados
//int old_tend_ter,oldestado;
//int ultimomes,ultimoano;
//double totalmes=0;
//double Puntocompra, Puntoventa, Profitcompra, Profitventa, salida;
//bool ActivaEMA;
//int BarCount;
//int Current;
//bool TickCheck = False;
double precioentrada; // indica el precio de la ultima entrada
double minim, maxim;
//double preu; //es el precio al que se hace la entrada

int registronuevo=0;
//int Nuevabarra;
double posicion=0.1;
int cambio=0;    
/*
/* aqui esta el codigo del mt4
/* */
//double ma,kcup,kcdo,deviation0,elstop;
//int corte_up,corte_do,corte_medio,elstop2;
//int giroup1=0,giroup2=0,giroup3=0,cont_giroup=0;
//int girodo1=0,girodo2=0,girodo3=0,cont_girodo=0;

//EMA = iMA(Symbol(), tiempo, periodos_EMA, 0, MODE_EMA, PRICE_CLOSE, 1); //para dibujar solo


//detección nueva barra de tiempo
//if (iTime(Symbol(),tiempo,0)!=oldTime)Nuevabarra=1;
//else Nuevabarra=0;
//oldTime=iTime(Symbol(),tiempo,0);   

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
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
    
    i=indice(instrument);
    if(i != 0 && instrument==strategyInstrument){  
        
    if(DebugMode==true) calendar.setTimeInMillis(tick.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
    if (estado_old[i] != estado[i])
        {
            estado_old[i]=estado[i];
            print(instrument+" cambio estado "+estado[i]+" hora y dia "+calendar.get(Calendar.HOUR_OF_DAY)+" y "+calendar.get(Calendar.DAY_OF_MONTH));
            //print ("will "+will[i]+" instrum "+i);
        }
    calculos(instrument,i,tick);
    if (estado[i] ==1 || estado[i]== 5) // busca compra
         {           
         if (tendencia[i]==2){estado[i]=3;}
         else  if (corte_do[i]==1){estado[i]=5;}
         }
               // estado 3 busca venta   
    else if (estado[i] ==3 || estado[i]== 6)
         {           
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
     
    public void calculos (Instrument instrument, int j, ITick close) throws JFException
    {
            if(DebugMode==true) calendar.setTimeInMillis(close.getTime());  // esto solo es necesario  cuando estamos en backtesting..
    else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));

     //will[j]= indicators.willr(instrument, Period.FOUR_HOURS, OfferSide.BID, PeriodoWill[j], 0);   
     will[j]= indicators.willr(instrument, Period.FOUR_HOURS, OfferSide.BID, PeriodoWill[j],Filter.ALL_FLATS,1,close.getTime(), 0)[0];   
     //will =iWPR(Symbol(),tiempo,periodos_EMA,0);       
 // el canal puede ser un starc o un bolinger
     //double ma = indicators.sma(instrument, Period.FOUR_HOURS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],0);
     double ma = indicators.sma(instrument, Period.FOUR_HOURS, OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0];
   //ma=iMA(Symbol(),tiempo,periodo_starc,0,0,PRICE_MEDIAN,0);
    //double deviation = indicators.atr(instrument, Period.FOUR_HOURS, OfferSide.BID,PeriodoStarc[j],0)*FactorAtr[j];
    double deviation = indicators.atr(instrument, Period.FOUR_HOURS, OfferSide.BID,PeriodoStarc[j],Filter.ALL_FLATS,1,close.getTime(),0)[0]*FactorAtr[j];
    //deviation0=iATR(NULL,tiempo,periodo_atr,0)*val_atr;
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
    //int hora =calendar.get(Calendar.HOUR_OF_DAY); 
    //int n = hora - ((int)(hora/4)*4);
    List <IBar> barra = history.getBars(instrument, Period.ONE_HOUR, OfferSide.BID, Filter.NO_FILTER, 8,history.getBarStart(Period.ONE_HOUR,close.getTime()),0);
    for(int k=0;k<4;k++)
           {
           maxbarra = barra.get(k).getHigh();
           minbarra = barra.get(k).getLow();    
           if (maximo < maxbarra) maximo=maxbarra;   
           if (minimo > minbarra) minimo=minbarra;
           }
     //IBar barra= history.getBar(instrument, Period.FOUR_HOURS, OfferSide.BID, 1);
      //      print(" maximo de 3 = "+ maximo+ " minimo de 3 ="+ minimo);
      //print(" maxim 4 = "+ barra.getHigh()+ " minimo 4 ="+ barra.getLow());

    if (close.getBid()>kcup[j]) corte_up[j]=1;
    if (close.getBid()<kcdo[j]) corte_do[j]=1;
    //if (close.getBid()>barra.getHigh())
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
          if (instrument == Instrument.EURUSD)
              return 1;
          if (instrument == Instrument.GBPUSD)
              return 2;
          if (instrument == Instrument.AUDUSD)
              return 3;
          if (instrument == Instrument.EURJPY)
              return 4;
          if (instrument == Instrument.GBPJPY)
              return 5;
          if (instrument == Instrument.USDCHF)
              return 6;
          else  return 0;                           
    }   
  

     // count opened positions
    protected int positionsTotal(Instrument instrument) throws JFException {
        int counter = 0;
        for (IOrder order : engine.getOrders(instrument)) {
            if (order.getState() == IOrder.State.FILLED) {
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
         print(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR)+"--"+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+
           " -state "+order.getState());       
        }    
 
    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label.substring(0, 2) + label.substring(3, 5);
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
    
/*    protected double lots(IBar bar) throws JFException
    {
    double Lots=0.01;
    double Lots2=0.01;
    if(MMType==1)Lots2=FixedLots;
    else if(MMType==2)Lots2=0.01*Math.sqrt(getBalance()/1000)*GeometricalFactor;
    else if(MMType==3)Lots2=0.1*equity/bar.getClose()/1000*ProportionalRisk/100;
    //if(MMType==3)Lots2=10000/tick.getAsk()/1000*ProportionalRisk/100;
    else if(MMType==4)Lots2=1000/100;
    Lots=arrondirLots(Lots2);
//    print("lotes "+Lots+" y sin redondeo="+ Lots2);
    return Lots;
    }
  */  
    public void onBar(Instrument instrument, Period period, IBar askbar, IBar bidbar) throws JFException {
    
i= indice(instrument);
//if(i != 0  && period==Period.FOUR_HOURS)
if(i != 0  && period==Period.ONE_HOUR)
   {    
   if(DebugMode==true) calendar.setTimeInMillis(bidbar.getTime());  // esto solo es necesario  cuando estamos en backtesting..
   else calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
   //print ("will "+will[i]+" instrum "+ i+" y hora"+calendar.get(Calendar.HOUR_OF_DAY)+" y "+calendar.get(Calendar.DAY_OF_MONTH));
    //print ("will "+will[i]);
    //print ("tendencia "+tendencia[i]);
   //if (calendar.get(Calendar.DAY_OF_WEEK)>=2 && calendar.get(Calendar.DAY_OF_WEEK)<=6) 
   int hora =calendar.get(Calendar.HOUR_OF_DAY); 
   int dia =calendar.get(Calendar.DAY_OF_MONTH);
   int mes =calendar.get(Calendar.MONTH);
   int dia_semana= calendar.get(Calendar.DAY_OF_WEEK);
   int n = hora - ((int)(hora/4)*4);
   //if (n == 2) 
   if (n == 1) 
       {   
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
             //print ("n vale "+n+" y son las "+calendar.get(Calendar.HOUR_OF_DAY));    
             posicion=Lots;
         //i=apertura_orden("", OP_BUY,posicion,1,StopLoss,TakeProfit);
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.BUY, Lots, 0, Slippage,askbar.getClose()
                        - Point * StopLoss[i], askbar.getClose() + Point * TakeProfit[i]);
             waitUntilOrderFilled(order);
             if (order.getState() == IOrder.State.FILLED)
                {estado[i]=2;}
         //preu=Ask;
             }
          }       
         // Estado 6 venta busca giro
      else if (estado[i]==6)
         {     
         if(girodo[i]==1 && will[i]<=-20)
             {
             posicion=Lots;
         //i=apertura_orden("", OP_SELL,posicion,1,StopLoss, TakeProfit);
             IOrder order = engine.submitOrder(getLabel(instrument),instrument,IEngine.OrderCommand.SELL, Lots, 0, Slippage,bidbar.getClose()
                        + Point * StopLoss[i], bidbar.getClose() - Point * TakeProfit[i]);
             waitUntilOrderFilled(order);
             if (order.getState() == IOrder.State.FILLED)
                {estado[i]=4;} 
         //preu = Bid; //guarda el precio de entrada
             }
          } 
         // Estado 7 para cerrar compra
      else if  (estado[i]==7)
          {
          if (kcup[i]>bidbar.getClose())
             {
          //OrderClose(i, posicion, Bid, Slippage, MediumSeaGreen);
              IOrder laorden= engine.getOrders(instrument).get(0);
              if(laorden.getOrderCommand() == IEngine.OrderCommand.BUY) 
                 {
                laorden.close();               
                waitUntilOrderFilled(laorden);
                estado[i]=1;
                }                 
             }
          }        
         // Estado 8 para cerrar venta
      else if (estado[i]==8)
          {
          if (kcdo[i]<bidbar.getClose())
             {
         //OrderClose(i, posicion, Ask, Slippage, MediumSeaGreen);
             IOrder laorden= engine.getOrders(instrument).get(0);
             if(laorden.getOrderCommand() == IEngine.OrderCommand.SELL) 
                {
                laorden.close();               
                waitUntilOrderFilled(laorden);
                estado[i]=3;
                }  
             }                     
         }      
      }    
   }   
}

   // public int normaliza_hora(

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
    }

    public void onAccount(IAccount account) throws JFException {
        this.equity = account.getEquity();
    }
    public void print(Object o) {

        this.console.getOut().println(o.toString());

    }

}