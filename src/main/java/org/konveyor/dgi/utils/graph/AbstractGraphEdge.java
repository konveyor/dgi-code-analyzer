/*
Copyright IBM Corporation 2023

Licensed under the Apache Public License 2.0, Version 2.0 (the "License");
you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.konveyor.dgi.utils.graph;

import java.io.Serializable;
import java.util.Map;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.shrike.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import org.jgrapht.nio.Attribute;
import com.ibm.wala.ipa.slicer.Statement;


public abstract class AbstractGraphEdge implements Serializable {
    public Integer weight = 1;
    public final String context;
    protected AbstractGraphEdge() {
        this(null);
    }
    protected AbstractGraphEdge(String context) {
        this.context = context;
    }
    public void incrementWeight() { this.weight += 1; }
    public String getContext() { return this.context; }
    public Integer getId() { return this.hashCode(); }
    public Integer getWeight() { return this.weight; }
    Integer getStatementPosition(Statement statement) {
        CGNode statementNode = statement.getNode();
        IR statementIR = statementNode.getIR();
        Integer pos = null;
        // TODO: check this assumption: the same source instruction maps to several SSAInstructions,
        //  therefore it is sufficient to return the position of the first statement.
        for (SSAInstruction inst : statementNode.getIR().getInstructions()) {
            try {
                pos = statementIR.getMethod().getSourcePosition(inst.iIndex()).getLastLine();
                return pos;
            } catch (InvalidClassFileException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException npe) {
                return -1;
            }
        }
        return pos;
    }

    public abstract Map<String, Attribute> getAttributes();
}
