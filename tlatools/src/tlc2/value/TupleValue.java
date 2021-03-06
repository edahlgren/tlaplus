// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Wed 12 Jul 2017 at 16:10:00 PST by ian morris nieves
//      modified on Mon 30 Apr 2007 at 15:30:09 PST by lamport
//      modified on Fri Aug 10 15:10:22 PDT 2001 by yuanyu

package tlc2.value;

import tlc2.output.EC;
import tlc2.output.MP;
import tlc2.tool.EvalControl;
import tlc2.tool.FingerprintException;
import tlc2.util.FP64;
import util.Assert;

public class TupleValue extends Value implements Applicable {
  public final Value[] elems;          // the elements of this tuple.

  /* Constructor */
  public TupleValue(Value[] elems) { this.elems = elems; }

  public TupleValue(Value v) {
	  this(new Value[1]);
    this.elems[0] = v;
  }

  public TupleValue(Value v1, Value v2) {
	  this(new Value[2]);
    this.elems[0] = v1;
    this.elems[1] = v2;
  }

  public final byte getKind() { return TUPLEVALUE; }

  public final int compareTo(Object obj) {
    try {
      TupleValue tv = obj instanceof Value ? ((Value)obj).toTuple() : null;
      if (tv == null) {
        // Well, we have to convert this to function and compare.
        return this.toFcnRcd().compareTo(obj);
      }
      int len = this.elems.length;
      int cmp = len - tv.elems.length;
      if (cmp == 0) {
        for (int i = 0; i < len; i++) {
          cmp = this.elems[i].compareTo(tv.elems[i]);
          if (cmp != 0) break;
        }
      }
      return cmp;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean equals(Object obj) {
    try {
      TupleValue tv = obj instanceof Value ? ((Value)obj).toTuple() : null;
      if (tv == null) {
        // Well, we have to convert this to function and compare.
        return this.toFcnRcd().equals(obj);
      }
      int len = this.elems.length;
      if (len != tv.elems.length)
        return false;
      for (int i = 0; i < len; i++) {
        if (!this.elems[i].equals(tv.elems[i]))
          return false;
      }
      return true;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean member(Value elem) {
    try {
      Assert.fail("Attempted to check set membership in a tuple value.");
      return false;   // make compiler happy
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean isFinite() { return true; }

  public final Value apply(Value arg, int control) {
    try {
      if (!(arg instanceof IntValue)) {
        Assert.fail("Attempted to apply tuple to a non-integer argument.");
      }
      int idx = ((IntValue)arg).val;
      if (idx <= 0 || idx > this.elems.length) {
        Assert.fail("Attempted to apply tuple\n" + ppr(this.toString()) +
        "\nto integer " + idx + " which is out of domain.");
      }
      return this.elems[idx-1];
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value apply(Value[] args, int control) {
    try {
      if (args.length != 1) {
        Assert.fail("Attetmpted to apply tuple with wrong number of arguments.");
      }
      return this.apply(args[0], EvalControl.Clear);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value select(Value arg) {
    try {
      if (!(arg instanceof IntValue)) {
        Assert.fail("Attempted to apply tuple to a non-integer argument " +
        ppr(arg.toString()) + ".");
      }
      int idx = ((IntValue)arg).val;
      if (idx > 0 && idx <= this.elems.length) {
        return this.elems[idx-1];
      }
      return null;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept ex) {
    try {
      if (ex.idx < ex.path.length) {
        int tlen = this.elems.length;
        Value[] newElems = new Value[tlen];
        Value arcVal = ex.path[ex.idx];
        if (arcVal instanceof IntValue) {
          int idx = ((IntValue)arcVal).val - 1;
          if (0 <= idx && idx < tlen) {
            for (int i = 0; i < tlen; i++) {
              newElems[i] = this.elems[i];
            }
            ex.idx++;
            newElems[idx] = this.elems[idx].takeExcept(ex);
          }
          return new TupleValue(newElems);
        }
        MP.printWarning(EC.TLC_WRONG_TUPLE_FIELD_NAME, new String[]{ppr(arcVal.toString())});
      }
      return ex.value;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value takeExcept(ValueExcept[] exs) {
    try {
      Value val = this;
      for (int i = 0; i < exs.length; i++) {
        val = val.takeExcept(exs[i]);
      }
      return val;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value getDomain() {
    try {
      return new IntervalValue(1, this.size());
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final int size() { return this.elems.length; }

  @Override
  public TupleValue toTuple() {
	  return this;
  }
  
  @Override
  public RecordValue toRcd() {
	  return size() == 0 ? EmptyRcd : super.toRcd();
  }

	@Override
	public FcnRcdValue toFcnRcd() {
        IntervalValue intv = new IntervalValue(1, this.elems.length);
        return new FcnRcdValue(intv, this.elems);
	}

  /* The normalization of the value. */
  public final boolean isNormalized() { return true; }

  public final Value normalize() { /*nop*/return this; }

  public final boolean isDefined() {
    try {
      boolean defined = true;
      for (int i = 0; i < this.elems.length; i++) {
        defined = defined && this.elems[i].isDefined();
      }
      return defined;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value deepCopy() {
    try {
      Value[] vals = new Value[this.elems.length];
      for (int i = 0; i < this.elems.length; i++) {
        vals[i] = this.elems[i].deepCopy();
      }
      return new TupleValue(vals);
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final boolean assignable(Value val) {
    try {
      boolean canAssign = ((val instanceof TupleValue) &&
         (this.elems.length == ((TupleValue)val).elems.length));
      if (!canAssign) return false;
      for (int i = 0; i < this.elems.length; i++) {
        canAssign = canAssign && this.elems[i].assignable(((TupleValue)val).elems[i]);
      }
      return canAssign;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  /* The fingerprint method: tuples are functions. */
  public final long fingerPrint(long fp) {
    try {
      int len = this.elems.length;
      fp = FP64.Extend(fp, FCNRCDVALUE);
      fp = FP64.Extend(fp, len);
      for (int i = 0; i < len; i++) {
        fp = FP64.Extend(fp, INTVALUE);
        fp = FP64.Extend(fp, i+1);
        fp = this.elems[i].fingerPrint(fp);
      }
      return fp;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  public final Value permute(MVPerm perm) {
    try {
      Value[] vals = new Value[this.elems.length];
      boolean changed = false;
      for (int i = 0; i < vals.length; i++) {
        vals[i] = this.elems[i].permute(perm);
        changed = changed || (vals[i] != this.elems[i]);
      }
      if (changed) {
        return new TupleValue(vals);
      }
      return this;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

  /* The string representation of this value. */
  public final StringBuffer toString(StringBuffer sb, int offset) {
    try {
      sb.append("<<");
      int len = this.elems.length;
      if (len > 0) {
        sb = this.elems[0].toString(sb, offset);
      }
      for (int i = 1; i < len; i++) {
        sb = sb.append(", ");
        sb = this.elems[i].toString(sb, offset);
      }
      sb.append(">>");
      return sb;
    }
    catch (RuntimeException | OutOfMemoryError e) {
      if (hasSource()) { throw FingerprintException.getNewHead(this, e); }
      else { throw e; }
    }
  }

}
