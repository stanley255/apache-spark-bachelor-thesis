package drools;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.Collection;

// Trieda na načítanie a aplikovanie biznis pravidiel z rozhodovacej tabuľky
public class Drools {
    // Načítanie bázy pravidiel
    public static KieBase loadRules() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer.getKieBase();
    }

    // Aplikovanie pravidiel na dáta
    public static void applyRules(KieBase base, Collection<Object> col) {
        StatelessKieSession session = base.newStatelessKieSession();
        session.execute(CommandFactory.newInsertElements(col));
    }

}
