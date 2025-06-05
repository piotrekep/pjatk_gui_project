package model;

import model.Point;
import java.util.Collection;
import java.util.Map;

/**
 * @class AgentModel
 * @brief Klasa exponuje liste agentów dla visuala
 *
 * @detailed Klasa AgentModel eksponuje odniesienia do agentów do modułu
 *           wizualnego
 */

public interface AgentModel {
    Collection<Agent> getAgents();
    Map<Point, Agent> getAgentsByLocation();
}
