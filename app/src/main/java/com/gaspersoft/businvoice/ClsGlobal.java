package com.gaspersoft.businvoice;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.gaspersoft.businvoice.models.InfoExcesoDto;
import com.gaspersoft.businvoice.models.InfoPasajeDto;
import com.gaspersoft.businvoice.utils.PrintHelper;
import com.gaspersoft.businvoice.utils.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Callback;

public class ClsGlobal extends Application {
    public static String instabus="";
    private static int print_size = 6;
    private static int error_level = 3;

    public static String getBaseUrl() {
        if (instabus.equals("")) {
            return "https://viaunooapi.gaspersoft.com";
        } else {
            return "https://instabusapi.gaspersoft.com";
        }
    }

    public static Integer getEmpresaId() {
        return 7;
    }

    public static boolean isRUCValid(long ruc){
        return isRUCValid(String.valueOf(ruc));
    }

    public static boolean isRUCValid(String ruc){
        if (ruc == null) {
            return false;
        }

        final int[] multipliers = {5,4,3,2,7,6,5,4,3,2};
        final String[] prefixes = getRucPrefixes();
        final int length = multipliers.length + 1;

        if(ruc.length() != length){
            return false;
        }

        boolean isPrefixOk = false;

        for (String prefix : prefixes){
            if(ruc.substring(0,2).equals(prefix)){
                isPrefixOk = true;
                break;
            }
        }

        if(!isPrefixOk){
            return false;
        }

        int sum = 0;

        for(int i = 0; i < multipliers.length; i++){
            final char section = ruc.charAt(i);

            if(!Character.isDigit(section)){
                return false;
            }

            sum += Character.getNumericValue(ruc.charAt(i)) * multipliers[i];
        }

        final int rest = sum % length;
        final String response = String.valueOf(length - rest);

        return response.charAt(response.length() - 1) == ruc.charAt(ruc.length() - 1);
    }

    public static String[] getRucPrefixes(){
        return new String[]{"10", "15", "17", "20"};
    }

    public static void ImprimirBoletoViaje(Context context, InfoPasajeDto infoPasaje) {

        if(PrintHelper.getInstance().sunmiPrinter==PrintHelper.NoSunmiPrinter) {
            Toast.makeText(context, "Impresora no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!BluetoothUtil.isBlueToothPrinter) {
            PrintHelper.getInstance().initPrinter();
            //0=Left  1=Center  2=Right
            PrintHelper.getInstance().setAlign(1);

            //Cabecera del documento
            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.viaunoo);

            if (!instabus.equals("")) {
                logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.intabusbg);
            }

            PrintHelper.getInstance().printBitmap(logo);
            PrintHelper.getInstance().printLine();
            PrintHelper.getInstance().printText(infoPasaje.empresaNombre + "\n", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.empresaDireccion + "\n", 22, false, false);
            PrintHelper.getInstance().printText("RUC:" + infoPasaje.empresaRuc + "\n", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.cpeNombreDocumento + "\n", 22, true, false);
            PrintHelper.getInstance().printText("N° " + infoPasaje.cpeNumeroDocumento + "\n", 22, true, false);


            if (infoPasaje.cpeTipoDocumentoId.equals("01")) {
                PrintHelper.getInstance().printLineDashed();
                PrintHelper.getInstance().printText("DATOS DE FACTURACION" + "\n", 22, true, false);
                PrintHelper.getInstance().setAlign(0);
                PrintHelper.getInstance().printText("FECHA EMISION: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.cpeFechaEmision + "\n", 22, false, false);
                PrintHelper.getInstance().printText("RUC: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.pasajeroRuc + "\n", 22, false, false);
                PrintHelper.getInstance().printText("RAZON SOCIAL: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.pasajeroRazonSocial + "\n", 22, false, false);
                PrintHelper.getInstance().printText("FORMA PAGO: ", 22, true, false);
                PrintHelper.getInstance().printText(infoPasaje.cpeFormaPago + "\n", 22, false, false);
            }

            PrintHelper.getInstance().printLineDashed();

            //Hora Impresion
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            String currentTime = df.format(calendar.getTime());

            //Informacion del pasajero
            PrintHelper.getInstance().setAlign(1);
            PrintHelper.getInstance().printText("INFORMACION DEL PASAJERO" + "\n", 22, true, false);
            PrintHelper.getInstance().setAlign(0);
            PrintHelper.getInstance().printText(infoPasaje.pasajeroTipoDocumento + ": ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeroNumeroDocumento + "\n", 22, false, false);
            PrintHelper.getInstance().printText("NOMBRE: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeroNombre + "\n", 22, false, false);
            PrintHelper.getInstance().printText("ORIGEN: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajePuntoOrigen + "\n", 22, false, false);
            PrintHelper.getInstance().printText("DESTINO: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajePuntoLlegada + "\n", 22, false, false);
            PrintHelper.getInstance().printText("FECHA DE VIAJE: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeFechaViaje + "\n", 22, false, false);
            PrintHelper.getInstance().printText("NUMERO DE ASIENTO: ", 22, true, false);
            PrintHelper.getInstance().printText(infoPasaje.pasajeNumeroAsiento + " ", 22, false, false);
            PrintHelper.getInstance().printText("IMP: ", 22, true, false);
            PrintHelper.getInstance().printText( currentTime + "\n", 22, false, false);

            //DESCRIPCION DEL SERVICIO
            int width[] = new int[]{1, 2, 1};
            int align[] = new int[]{1, 0, 2};
            PrintHelper.getInstance().printLineDashed();

            String cabecera[] = new String[]{"CANT.", "DESCRIPCION", "P. UNT."};
            PrintHelper.getInstance().printColumnsString(cabecera, width, align, true);

            PrintHelper.getInstance().printLineDashed();
            String detalle[] = new String[]{"1", infoPasaje.cpeDescripcionServicio, infoPasaje.cpeImporteTotal};
            PrintHelper.getInstance().printColumnsString(detalle, width, align, false);

            //Totales
            width = new int[]{3, 1};
            align = new int[]{2, 2};
            PrintHelper.getInstance().printLineDashed();
            String operacionesExoneradas[] = new String[]{"OP. EXONERADAS: "+ infoPasaje.cpeSimboloMoneda, infoPasaje.cpeTotalOperacionesExoneradas};
            String sumatoriaIGV[] = new String[]{"IGV " + infoPasaje.cpeTasaIgv+"%: "+ infoPasaje.cpeSimboloMoneda, infoPasaje.cpeSumatoriaIgv};
            String importeTotal[] = new String[]{"IMPORTE TOTAL: "+ infoPasaje.cpeSimboloMoneda, infoPasaje.cpeImporteTotal};
            PrintHelper.getInstance().printColumnsString(operacionesExoneradas, width, align, false);
            PrintHelper.getInstance().printColumnsString(sumatoriaIGV, width, align, false);
            PrintHelper.getInstance().printColumnsString(importeTotal, width, align, true);

            //Codigo Qr
            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().setAlign(1);
            PrintHelper.getInstance().printQr(infoPasaje.cpeResumenQr, print_size, error_level);
            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().printText(infoPasaje.cpeUrlConsulta + "\n", 22, false, false);
            PrintHelper.getInstance().feedPaper();

        } else {
            Toast.makeText(context, "Error de impresora", Toast.LENGTH_SHORT).show();
        }
    }

    public static void ImprimirExceso(Context context, InfoExcesoDto infoExceso) {

        if (PrintHelper.getInstance().sunmiPrinter == PrintHelper.NoSunmiPrinter) {
            Toast.makeText(context, "Impresora no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!BluetoothUtil.isBlueToothPrinter) {
            PrintHelper.getInstance().initPrinter();
            //0=Left  1=Center  2=Right
            PrintHelper.getInstance().setAlign(1);

            //Cabecera del documento
            Bitmap logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.viaunoo);

            if (!instabus.equals("")) {
                logo = BitmapFactory.decodeResource(context.getResources(), R.drawable.intabusbg);
            }

            PrintHelper.getInstance().printBitmap(logo);
            PrintHelper.getInstance().printLine();
            PrintHelper.getInstance().printText(infoExceso.empresaNombre + "\n", 22, true, false);
            PrintHelper.getInstance().printText(infoExceso.empresaDireccion + "\n", 22, false, false);
            PrintHelper.getInstance().printText("RUC:" + infoExceso.empresaRuc + "\n", 22, true, false);
            PrintHelper.getInstance().printText(infoExceso.cpeNombreDocumento + "\n", 22, true, false);
            PrintHelper.getInstance().printText("N° " + infoExceso.cpeNumeroDocumento + "\n", 22, true, false);

            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().setAlign(0);
            PrintHelper.getInstance().printText("FECHA EMISION: ", 22, true, false);
            PrintHelper.getInstance().printText(infoExceso.cpeFechaEmision + "\n", 22, false, false);
            PrintHelper.getInstance().printText(infoExceso.clienteTipoDocumento + ": ", 22, true, false);
            PrintHelper.getInstance().printText(infoExceso.clienteNumeroDocumento + "\n", 22, false, false);
            PrintHelper.getInstance().printText("NOMBRE/RAZON SOCIAL: ", 22, true, false);
            PrintHelper.getInstance().printText(infoExceso.clienteNombre + "\n", 22, false, false);
            PrintHelper.getInstance().printText("FORMA PAGO: ", 22, true, false);
            PrintHelper.getInstance().printText(infoExceso.cpeFormaPago + "\n", 22, false, false);

            //DESCRIPCION DEL SERVICIO
            int width[] = new int[]{1, 2, 1};
            int align[] = new int[]{1, 0, 2};
            PrintHelper.getInstance().printLineDashed();

            String cabecera[] = new String[]{"CANT.", "DESCRIPCION", "P. UNT."};
            PrintHelper.getInstance().printColumnsString(cabecera, width, align, true);

            PrintHelper.getInstance().printLineDashed();
            String detalle[] = new String[]{"1", infoExceso.cpeDescripcionServicio, infoExceso.cpeImporteTotal};
            PrintHelper.getInstance().printColumnsString(detalle, width, align, false);

            //Totales
            width = new int[]{3, 1};
            align = new int[]{2, 2};
            PrintHelper.getInstance().printLineDashed();
            String operacionesExoneradas[] = new String[]{"OP. GRAVADAS: " + infoExceso.cpeSimboloMoneda, infoExceso.cpeTotalOperacionesGravadas};
            String sumatoriaIGV[] = new String[]{"IGV " + infoExceso.cpeTasaIgv + "%: " + infoExceso.cpeSimboloMoneda, infoExceso.cpeSumatoriaIgv};
            String importeTotal[] = new String[]{"IMPORTE TOTAL: " + infoExceso.cpeSimboloMoneda, infoExceso.cpeImporteTotal};
            PrintHelper.getInstance().printColumnsString(operacionesExoneradas, width, align, false);
            PrintHelper.getInstance().printColumnsString(sumatoriaIGV, width, align, false);
            PrintHelper.getInstance().printColumnsString(importeTotal, width, align, true);

            //Codigo Qr
            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().setAlign(1);
            PrintHelper.getInstance().printQr(infoExceso.cpeResumenQr, print_size, error_level);
            PrintHelper.getInstance().printLineDashed();
            PrintHelper.getInstance().printText(infoExceso.cpeUrlConsulta + "\n", 22, false, false);
            PrintHelper.getInstance().feedPaper();

        } else {
            Toast.makeText(context, "Error de impresora", Toast.LENGTH_SHORT).show();
        }
    }
}
