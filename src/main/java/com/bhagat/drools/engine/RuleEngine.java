package com.bhagat.drools.engine;

import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

import com.bhagat.drools.domain.CartItem;

/**
 * The RuleEngine executes the rules.
 */
public class RuleEngine {
	private static KieSession kSession = null;

	public static int fireSingleRule(final String ruleName) {
		if (kSession != null) {

			return kSession.fireAllRules(new AgendaFilter() {
				public boolean accept(Match match) {
					if (ruleName!=null && ruleName.equalsIgnoreCase(match.getRule().getName())) {
						System.out.println("Firing Rule: " + match.getRule().getName());
						return true;
					}
					return false;
				}
			});
		}
		return 0;
	}

	public static KieSession getKieSession() {
		if (kSession == null) {
			// Instantiate KIE Service and create a new rule session
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			kSession = kContainer.newKieSession("rules-session");
		}
		return kSession;
	}

	public static void registerKSessionEvents() {
		/**
		 * Register Events
		 */
		if (kSession != null) {
			kSession.addEventListener(new RuleRuntimeEventListener() {
				public void objectInserted(ObjectInsertedEvent event) {
					if (event.getObject() instanceof CartItem) {
						System.out.println(" ############ Rule is Inserted: " + event.getObject());
					}
				}

				public void objectUpdated(ObjectUpdatedEvent event) {
					System.out.println("############  Rule is Updated: " + event.getRule().getName());
				}

				public void objectDeleted(ObjectDeletedEvent event) {
					System.out.println("############  Rule is Deleted: " + event.getRule().getName());
				}
			});
		}
	}

	public static <I> void insert(final I item) {
		kSession.insert(item);

	}

	public static int fireAllRules() {
		System.out.println("#### Fire All Rules ####");
		int result = kSession.fireAllRules();
		System.out.println("-------------------------------------");
		return result;

	}

	public static <R> boolean isNotNull(final List<R> coll) {
		return coll != null && !coll.isEmpty() ? true : false;
	}
}