package model;

import java.util.Collection;

/**
 * @class AgentModel
 * @brief Klasa exponuje liste agentów dla visuala
 *
 * @detailed Klasa AgentModelo eksponuje odniesienia do agentów do modułu
 *           wizualnego
 */

public interface AgentModel {
    Collection<Agent> getAgents();
}
