package com.odontologia.odontologia.config;

import com.odontologia.odontologia.model.DentalService;
import com.odontologia.odontologia.repository.DentalServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ServiceCatalogInitializer {

    @Bean
    CommandLineRunner initDentalServices(DentalServiceRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                log.info("Service catalog already exists ({} records)", repo.count());
                return;
            }

            log.info("Loading dental service catalog...");
            int order;

            order = 0;
            String OG = "ODONTOLOGIA_GENERAL";
            save(repo, OG, "Consulta dental",                          bd("10.00"),  "PEN", ++order);
            save(repo, OG, "Resina Simple",                            bd("25.00"),  "PEN", ++order);
            save(repo, OG, "Resina Compuesta",                         bd("45.00"),  "PEN", ++order);
            save(repo, OG, "Resina Compleja",                          bd("65.00"),  "PEN", ++order);
            save(repo, OG, "Cambio de resina simple",                  bd("60.00"),  "PEN", ++order);
            save(repo, OG, "Cambio de resina compuesta",               bd("80.00"),  "PEN", ++order);
            save(repo, OG, "Cambio de amalgama simple",                bd("60.00"),  "PEN", ++order);
            save(repo, OG, "Cambio de amalgama compuesta",             bd("80.00"),  "PEN", ++order);
            save(repo, OG, "Radiografía periapical",                   bd("20.00"),  "PEN", ++order);
            save(repo, OG, "Resina cervical",                          bd("25.00"),  "PEN", ++order);
            save(repo, OG, "Resina estética",                          bd("80.00"),  "PEN", ++order);
            save(repo, OG, "Profilaxis + Destartraje + Flúor gel",     bd("60.00"),  "PEN", ++order);
            save(repo, OG, "Profilaxis + Destartraje + Flúor barniz",  bd("90.00"),  "PEN", ++order);
            save(repo, OG, "Obturación provisional",                   bd("20.00"),  "PEN", ++order);

            order = 0;
            String ORT = "ORTODONCIA";
            save(repo, ORT, "Ortodoncia cuota inicial",                   bd("500.00"), "PEN", ++order);
            save(repo, ORT, "Ortodoncia cuota mensual",                   bd("150.00"), "PEN", ++order);
            save(repo, ORT, "Modelo de estudio",                          bd("100.00"), "PEN", ++order);
            save(repo, ORT, "Kit radiográfico ortodoncia",                bd("180.00"), "PEN", ++order);
            save(repo, ORT, "Consulta con especialista",                  bd("30.00"),  "PEN", ++order);
            save(repo, ORT, "Contención con férula sup e inf",            bd("500.00"), "PEN", ++order);
            save(repo, ORT, "Mini implante",                              bd("300.00"), "PEN", ++order);
            save(repo, ORT, "Pegado de bracket nuevo en su cita",         bd("20.00"),  "PEN", ++order);
            save(repo, ORT, "Pegado de bracket nuevo fuera de su cita",   bd("35.00"),  "PEN", ++order);
            save(repo, ORT, "Retiro de brackets",                         bd("100.00"), "PEN", ++order);

            order = 0;
            String EXT = "EXTRACCION";
            save(repo, EXT, "Extracción adultos incisivos y caninos",  bd("80.00"),  "PEN", ++order);
            save(repo, EXT, "Extracción adultos premolares",           bd("100.00"), "PEN", ++order);
            save(repo, EXT, "Extracción adultos molares",              bd("150.00"), "PEN", ++order);
            save(repo, EXT, "Extracción muelas del juicio",            bd("250.00"), "PEN", ++order);
            save(repo, EXT, "Extracción niños simples",                bd("70.00"),  "PEN", ++order);
            save(repo, EXT, "Extracción niños complejas",              bd("100.00"), "PEN", ++order);

            order = 0;
            String END = "ENDODONCIA";
            save(repo, END, "Incisivos",                                                    bd("300.00"), "PEN", ++order);
            save(repo, END, "Premolares",                                                   bd("320.00"), "PEN", ++order);
            save(repo, END, "Molares",                                                      bd("350.00"), "PEN", ++order);
            save(repo, END, "Retratamiento",                                                bd("500.00"), "PEN", ++order);
            save(repo, END, "Cx conductos atresicos, radix, premolar 3 conductos, inf crónica + 3 citas", bd("50.00"), "PEN", ++order);
            save(repo, END, "Gingivectomía simple",                                         bd("100.00"), "PEN", ++order);
            save(repo, END, "Gingivectomía compleja",                                       bd("150.00"), "PEN", ++order);
            save(repo, END, "Tapón biocerámico",                                            bd("150.00"), "PEN", ++order);

            order = 0;
            String REH = "REHABILITACION";
            save(repo, REH, "Espigo metal/fibra",                bd("250.00"), "PEN", ++order);
            save(repo, REH, "Corona M/P",                        bd("500.00"), "PEN", ++order);
            save(repo, REH, "Corona Zirconio",                   bd("700.00"), "PEN", ++order);
            save(repo, REH, "Incrustación cerómero",             bd("350.00"), "PEN", ++order);
            save(repo, REH, "Prótesis removible unidad",         bd("900.00"), "PEN", ++order);
            save(repo, REH, "Prótesis total unidad",             bd("900.00"), "PEN", ++order);

            order = 0;
            String IMP = "IMPLANTES";
            save(repo, IMP, "Implante + Corona",                 bd("1400.00"), "USD", ++order);

            order = 0;
            String PRD = "PRODUCTOS";

            save(repo, PRD, "Vitis Neceser Aloe Vera",              bd("35.00"),  "PEN", ++order);
            save(repo, PRD, "Vitis Neceser Orthodontic",            bd("40.00"),  "PEN", ++order);
            save(repo, PRD, "Vitis Neceser Implanto",               bd("0.00"),   "PEN", ++order);
            save(repo, PRD, "Vitis Neceser Junior (+06)",           bd("30.00"),  "PEN", ++order);
            save(repo, PRD, "Vitis Neceser Kids (+02)",             bd("30.00"),  "PEN", ++order);

            save(repo, PRD, "Perio Aid Intensivo/Control 150ml",   bd("25.00"),  "PEN", ++order);

            save(repo, PRD, "Enjuague Vitis CPC Protect 500ml",    bd("30.00"),  "PEN", ++order);
            save(repo, PRD, "Enjuague Vitis CPC Junior (+06)",     bd("35.00"),  "PEN", ++order);
            save(repo, PRD, "Enjuague Vitis CPC Sensible",         bd("30.00"),  "PEN", ++order);
            save(repo, PRD, "Enjuague Vitis CPC Orthodontic",      bd("30.00"),  "PEN", ++order);

            save(repo, PRD, "Enjuague Vitis Encías 150ml",         bd("20.00"),  "PEN", ++order);

            save(repo, PRD, "Cepillo Vitis Baby",                  bd("15.00"),  "PEN", ++order);
            save(repo, PRD, "Cepillo Vitis Medio",                 bd("15.00"),  "PEN", ++order);
            save(repo, PRD, "Cepillo Vitis Junior",                bd("15.00"),  "PEN", ++order);

            save(repo, PRD, "Pasta Orthodontic",                   bd("20.00"),  "PEN", ++order);
            save(repo, PRD, "Pasta Kids (+02)",                    bd("15.00"),  "PEN", ++order);
            save(repo, PRD, "Pasta Blanqueadora",                  bd("20.00"),  "PEN", ++order);
            save(repo, PRD, "Pasta Vitis Junior",                  bd("17.00"),  "PEN", ++order);

            save(repo, PRD, "Interprox",                           bd("25.00"),  "PEN", ++order);
            save(repo, PRD, "Interprox Amarillo",                  bd("15.00"),  "PEN", ++order);

            save(repo, PRD, "Hilo Dental Colgate",                 bd("13.00"),  "PEN", ++order);
            save(repo, PRD, "Enjuague Colgate Plax Max Soft Mint", bd("18.00"),  "PEN", ++order);

            log.info("Service catalog loaded: {} dental services", repo.count());
        };
    }

    private void save(DentalServiceRepository repo, String category, String name,
                      BigDecimal price, String currency, int order) {
        DentalService s = new DentalService();
        s.setCategory(category);
        s.setName(name);
        s.setUnitPrice(price);
        s.setCurrency(currency);
        s.setSortOrder(order);
        s.setStatus(true);
        repo.save(s);
    }

    private BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
