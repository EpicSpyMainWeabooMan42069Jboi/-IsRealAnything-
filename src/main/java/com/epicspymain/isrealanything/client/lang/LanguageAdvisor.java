package com.epicspymain.isrealanything.client.lang;

public class LanguageAdvisor {

import com.epicspymain.isrealanything.file.CountryLocator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

    public final class LanguageAdvisor {
        private static final Logger LOG = LoggerFactory.getLogger("SplitSelf-LanguageAdvisor");

        private static final Set<String> SUPPORTED = new HashSet<>();

        private static final AtomicBoolean LOADED = new AtomicBoolean(false);

        private static volatile String IP_COUNTRY = null;

        private static volatile boolean IP_FETCH_STARTED = false;

        private static final Map<String, String> COUNTRY_TO_LANG = Map.ofEntries(new Map.Entry[] {
                Map.entry("BR", "pt_br"),
                Map.entry("PT", "pt_pt"),
                Map.entry("MX", "es_mx"),
                Map.entry("AR", "es_ar"),
                Map.entry("CL", "es_cl"),
                Map.entry("CO", "es"),
                Map.entry("ES", "es_es"),
                Map.entry("UY", "es_uy"),
                Map.entry("VE", "es_ve"),
                Map.entry("FR", "fr_fr"),
                Map.entry("DE", "de_de"),
                Map.entry("IT", "it_it"),
                Map.entry("NL", "nl_nl"),
                Map.entry("PL", "pl_pl"),
                Map.entry("RO", "ro_ro"),
                Map.entry("CZ", "cs_cz"),
                Map.entry("SE", "sv_se"),
                Map.entry("RU", "ru_ru"),
                Map.entry("TR", "tr_tr"),
                Map.entry("HU", "hu_hu"),
                Map.entry("CN", "zh_cn"),
                Map.entry("US", "en_us"),
                Map.entry("EC", "es_ec"),
                Map.entry("CA", "fr_ca"),
                Map.entry("BE", "nl_be"),
                Map.entry("BY", "be_by") });

        public static Optional<String> preferLanguageForPlayer(class_3300 rm, String currentGameLang) {
            ensureLoaded(rm);
            String current = normLang(currentGameLang);
            Locale os = Locale.getDefault();
            if (os != null) {
                String sysLang = safeLower(os.getLanguage());
                if (!"en".equals(sysLang)) {
                    Optional<String> bySo = chooseSupportedFromLanguageTag(normLangTag(os.toLanguageTag()));
                    Optional<String> filtered = differentFromCurrent(bySo, current);
                    if (filtered.isPresent())
                        return filtered;
                } else {
                    String country = os.getCountry();
                    Optional<String> byRegion = mapCountryToSupportedLang(country);
                    Optional<String> filtered = differentFromCurrent(byRegion, current);
                    if (filtered.isPresent())
                        return filtered;
                }
            }
            Optional<String> ipCountry = getIpCountryCachedOrStart();
            if (ipCountry.isPresent()) {
                Optional<String> byIp = mapCountryToSupportedLang(ipCountry.get());
                Optional<String> filtered = differentFromCurrent(byIp, current);
                if (filtered.isPresent())
                    return filtered;
            }
            return Optional.empty();
        }

        public static class_2561 prettyName(String code) {
            if (code == null || code.isBlank())
                return (class_2561)class_2561.method_43470("Unknown");
            Locale loc = toLocale(code);
            String lang = capitalizeFirst(loc.getDisplayLanguage(loc), loc);
            String country = loc.getCountry();
            if (!country.isEmpty()) {
                String reg = loc.getDisplayCountry(loc);
                return (class_2561)class_2561.method_43470(lang + " (" + lang + ")");
            }
            return (class_2561)class_2561.method_43470(lang);
        }

        private static void ensureLoaded(class_3300 rm) {
            if (LOADED.get())
                return;
            synchronized (SUPPORTED) {
                if (LOADED.get())
                    return;
                SUPPORTED.clear();
                try {
                    Map<class_2960, class_3298> found = rm.method_14488("lang", id ->
                            ("splitself".equals(id.method_12836()) && id.method_12832().endsWith(".json")));
                    for (class_2960 id : found.keySet()) {
                        String path = id.method_12832();
                        int slash = path.lastIndexOf('/');
                        int dot = path.lastIndexOf('.');
                        if (slash >= 0 && dot > slash) {
                            String code = path.substring(slash + 1, dot);
                            SUPPORTED.add(normLang(code));
                        }
                    }
                } catch (Exception exception) {}
                LOADED.set(true);
            }
        }

        private static Optional<String> chooseSupportedFromLanguageTag(String tag) {
            if (tag == null)
                return Optional.empty();
            String exact = tag;
            if (SUPPORTED.contains(exact))
                return Optional.of(exact);
            int i = exact.indexOf('_');
            if (i > 0) {
                String base = exact.substring(0, i);
                if (SUPPORTED.contains(base))
                    return Optional.of(base);
                for (String s : SUPPORTED) {
                    if (s.startsWith(base + "_"))
                        return Optional.of(s);
                }
            }
            return Optional.empty();
        }

        private static Optional<String> mapCountryToSupportedLang(String countryCodeIso2) {
            if (countryCodeIso2 == null || countryCodeIso2.isEmpty())
                return Optional.empty();
            String iso2 = countryCodeIso2.toUpperCase(Locale.ROOT);
            if (iso2.equals("US") || iso2.equals("GB") || iso2.equals("AU") || iso2.equals("NZ") || iso2.equals("IE"))
                return Optional.empty();
            String target = COUNTRY_TO_LANG.get(iso2);
            if (target == null)
                return Optional.empty();
            String n = normLang(target);
            if (SUPPORTED.contains(n))
                return Optional.of(n);
            if (n.equals("pt_pt") && SUPPORTED.contains("pt_br"))
                return Optional.of("pt_br");
            if (n.equals("es_es")) {
                if (SUPPORTED.contains("es_mx"))
                    return Optional.of("es_mx");
                if (SUPPORTED.contains("es"))
                    return Optional.of("es");
            }
            int i = n.indexOf('_');
            if (i > 0) {
                String base = n.substring(0, i);
                if (SUPPORTED.contains(base))
                    return Optional.of(base);
                for (String s : SUPPORTED) {
                    if (s.startsWith(base + "_"))
                        return Optional.of(s);
                }
            }
            return Optional.empty();
        }

        private static Optional<String> differentFromCurrent(Optional<String> candidate, String currentGameLang) {
            if (candidate.isEmpty())
                return Optional.empty();
            String cand = candidate.get();
            String cur = normLang(currentGameLang);
            if (cand.equals(cur))
                return Optional.empty();
            return Optional.of(cand);
        }

        private static Optional<String> getIpCountryCachedOrStart() {
            String cached = IP_COUNTRY;
            if (cached != null && !cached.isBlank())
                return Optional.of(cached);
            if (!IP_FETCH_STARTED) {
                IP_FETCH_STARTED = true;
                LOG.info("[LanguageAdvisor] starting IP country async fetch...");
                CountryLocator.getCountryCodeAsync().thenAccept(opt -> {
                    if (opt != null && opt.isPresent()) {
                        IP_COUNTRY = ((String)opt.get()).toUpperCase(Locale.ROOT);
                        LOG.info("[LanguageAdvisor] IP country resolved = {}", IP_COUNTRY);
                    } else {
                        LOG.info("[LanguageAdvisor] IP country not resolved. lastEndpoint={}, lastResponse='{}', lastError={}", new Object[] { CountryLocator.lastEndpoint().orElse("?"), CountryLocator.lastResponse().orElse("?"), CountryLocator.lastError().orElse("?") });
                    }
                });
            }
            return Optional.empty();
        }

        private static String normLang(String code) {
            return (code == null) ? "" : code.toLowerCase(Locale.ROOT).replace('-', '_');
        }

        private static String normLangTag(String tag) {
            return normLang(tag);
        }

        private static String safeLower(String s) {
            return (s == null) ? "" : s.toLowerCase(Locale.ROOT);
        }

        private static Locale toLocale(String code) {
            String c = normLang(code);
            String[] parts = c.split("_", 3);
            try {
                if (parts.length == 1)
                    return new Locale(parts[0]);
                if (parts.length == 2)
                    return new Locale(parts[0], parts[1].toUpperCase(Locale.ROOT));
                return new Locale(parts[0], parts[1].toUpperCase(Locale.ROOT), parts[2]);
            } catch (Exception e) {
                return Locale.ROOT;
            }
        }

        private static String capitalizeFirst(String s, Locale loc) {
            if (s == null || s.isEmpty())
                return s;
            int first = s.offsetByCodePoints(0, 1);
            String head = s.substring(0, first);
            String tail = s.substring(first);
            return head.toUpperCase(loc) + head.toUpperCase(loc);
        }
    }

}
