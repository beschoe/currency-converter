Read .github\copilot-instructions.md and docs\INC_ISSUE_AUTHORING.md to support me in writing the following issue description:

This code for currency conversion currently can only ingest and process currency exchange rates with regard to one canonical base currency. This creates major application constraints: 

Cross-rates for currency pairs can only be calculated if for both currencies explicit exchange rates against this canonical base currency are provided. For example, USD->GBP can be calculated if EUR->USD and EUR->GBP are given. But USD->GBP cannot be ingested directly, even if it is directly available from currency markets,  due to the canonical  base currency constraint. 

Lifting this constraint enables currency pair calculations along combinatorial chains. For example, if USD->CHF, EUR->USD and EUR->GBP can be provided as ingested data, synthetic exchange rates for currency pairs like CHF->GBP can be inferred by combining chains like EUR->CHF = (EUR->USD)->(USD->CHF) and then by inverting (CHF->EUR)->(EUR->GBP)=CHF->GBP.

Goal: Lift the constraint of only ingesting currency pairs with a canonical base currency, so that unrestricted currency pairs can be ingested. Based on lifting this constraint currency exchange, enable the implementation of getExchangeRate(from, to) to also provide also calculated synthetic exchange rates for all currency pairs for which a synthetic calculation path can be constructed from the ingested currency pair data. 

Implementation Guardrails: Update the helper functions and their structure where necessary to inject the necessary algorithmic capability for on-the-fly combinatorial construction of synthetic pairs. For synthetic pairs, all calculation shall be on-the-fly without further precalculated caching.

 One side-goal is important when you now go into issue description authoring: This issue is used for testing the capability of Github Coding Agent for its self-organization of implementation strategy for more complex requirements-based issues. Therefore when you now write the issue, do not go into any technical details beyond what I described here, express explicitly that all further algorithmic and technology-related design choices shall be fully covered by the Github Coding Agent.