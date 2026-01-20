package com.xiaosa.filmagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ReActAgent extends BaseAgent{
    // 思考当前状态决定是否下一步行动，true执行，false不执行
    public abstract boolean think();
    // 执行的结果
    public abstract String act();

    @Override
    public String step(){
        try{
            boolean shouldAct = think();
            if(!shouldAct){
                return "No action needed";
            }
            return act();
        }catch (Exception e){
            e.printStackTrace();
            return "Error in ReActAgent: "+e.getMessage();
        }
    }

}
