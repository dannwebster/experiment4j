package com.ticketmaster.exp.util;

import com.ticketmaster.exp.ReturnChoice;
import com.ticketmaster.exp.TrialType;

public class AlwaysControlReturnChoice implements ReturnChoice {

  @Override
  public boolean async() {
    return true;
  }

  @Override
  public TrialType choice() {
    return TrialType.CONTROL;
  }

}
