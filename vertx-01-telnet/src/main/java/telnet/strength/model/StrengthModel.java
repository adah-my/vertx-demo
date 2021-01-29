package telnet.strength.model;

import telnet.strength.model.impl.StrengthModelImpl;

/**
 * @author muyi
 * @description:
 * @date 2020-11-06 18:22:23
 */
public interface StrengthModel {

    static StrengthModel getInstance() {
        return StrengthModelImpl.getInstance();
    }

    void minusOneStrength(String userId);

    int getUserStrength(String userId);
}
