package org.emoflon.ibex.tgg.benchmark.runner.util;

import java.util.function.Consumer;

import org.benchmarx.companyLanguage.core.CompanyLanguageHelper;
import org.benchmarx.simplefamilies.core.SimpleFamiliesHelper;
import org.benchmarx.itLanguage.core.ITLanguageHelper;
import org.benchmarx.simplepersons.core.SimplePersonsHelper;
import org.eclipse.emf.ecore.EObject;

import CompanyLanguage.Company;
import ITLanguage.IT;
import SimpleFamilies.FamilyRegister;
import SimplePersons.PersonRegister;

public class IncrementalEditor {
	
	private CompanyLanguageHelper companies = new CompanyLanguageHelper();
	private ITLanguageHelper itHelper = new ITLanguageHelper();
	
	private SimpleFamiliesHelper families = new SimpleFamiliesHelper();
	private SimplePersonsHelper persons = new SimplePersonsHelper();
	
	public Consumer<EObject> getEdit(String tggName, boolean isFwd) {
		switch (tggName) {
			case "CompanyToIT":
				if (isFwd)
					return this::companyAddEmployee;
				else
					return this::itAddLaptop;
			case "FamiliesToPersons_V0":
			case "FamiliesToPersons_V1":
				if (isFwd)
					return this::familiesAddDaughter;
				else
					return this::personsAddDaughter;
			default:
				return (e) -> {};
		}
	}
	
	public void companyAddEmployee(EObject company) {
		companies.createEmployeeForFirstCEO((Company)company, "newEmployee");
	}

	public void itAddLaptop(EObject it) {
		itHelper.createLaptopOnFirstNetwork((IT)it, "newLaptop");
	}
	
	public void familiesAddDaughter(EObject familyRegister) {
		families.createDaughterInSingleTestFamily((FamilyRegister)familyRegister, "newDaughter");
	}
	
	public void personsAddDaughter(EObject personRegister) {
		persons.createFemale((PersonRegister)personRegister, "newDaughter");
	}
}
