/*******************************************************************************
 * Copyright (c) 2005, 2009 Andrea Bittau, University College London, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andrea Bittau - initial API and implementation from the PsychoPath XPath 2.0
 *     Mukul Gandhi - bug 274805 - improvements to xs:integer data type
 *     Mukul Gandhi - bug 279406 - improvements to negative zero values for xs:float
 *     David Carver - bug 262765 - fixed rounding errors.
 *     David Carver - bug 282223 - fixed casting errors.
 *     Jesper Steen Moller - Bug 286062 - Fix idiv error cases and increase precision  
 *******************************************************************************/

package org.eclipse.wst.xml.xpath2.processor.internal.types;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;

import org.eclipse.wst.xml.xpath2.processor.DynamicContext;
import org.eclipse.wst.xml.xpath2.processor.DynamicError;
import org.eclipse.wst.xml.xpath2.processor.ResultSequence;
import org.eclipse.wst.xml.xpath2.processor.ResultSequenceFactory;
import org.eclipse.wst.xml.xpath2.processor.internal.*;

/**
 * A representation of the Float datatype
 */
public class XSFloat extends NumericType {

	private Float _value;
	private XPathDecimalFormat format = new XPathDecimalFormat("0.#######E0");
	/**
	 * Initiates a representation of the supplied number
	 * 
	 * @param x
	 *            The number to be stored
	 */
	public XSFloat(float x) {
		_value = x;
	}

	/**
	 * Initiates a representation of 0
	 */
	public XSFloat() {
		this(0);
	}

	/**
	 * Retrieves the datatype's full pathname
	 * 
	 * @return "xs:float" which is the datatype's full pathname
	 */
	@Override
	public String string_type() {
		return "xs:float";
	}

	/**
	 * Retrieves the datatype's name
	 * 
	 * @return "float" which is the datatype's name
	 */
	@Override
	public String type_name() {
		return "float";
	}

	/**
	 * Retrieves a String representation of the stored number
	 * 
	 * @return String representation of the stored number
	 */
	@Override
	public String string_value() {
		if (zero()) {
		   return "0";
		}
		if (negativeZero()) {
		   return "-0";	
		}
		
		if (nan()) {
		   return "NaN";	
		}
								
		return format.xpathFormat(_value);
	}

	/**
	 * Check for whether this datatype represents NaN
	 * 
	 * @return True is this datatype represents NaN. False otherwise
	 */
	public boolean nan() {
		return Float.isNaN(_value);
	}

	/**
	 * Check for whether this datatype represents negative or positive infinity
	 * 
	 * @return True is this datatype represents infinity. False otherwise
	 */
	public boolean infinite() {
		return Float.isInfinite(_value);
	}

	/**
	 * Check for whether this datatype represents 0
	 * 
	 * @return True if this datatype represents 0. False otherwise
	 */
	@Override
	public boolean zero() {
	   return (Float.compare(_value, 0) == 0);
	}
	
	/*
	 * Check for whether this XSFloat represents -0
	 * 
	 * @return True if this XSFloat represents -0. False otherwise.
	 * @since 1.1
	 */
	public boolean negativeZero() {
	   return (Float.compare(_value, -0.0f) == 0);
	}
	
	/**
	 * Creates a new ResultSequence consisting of the retrievable float in the
	 * supplied ResultSequence
	 * 
	 * @param arg
	 *            The ResultSequence from which to extract the float
	 * @return New ResultSequence consisting of the float supplied
	 * @throws DynamicError
	 */
	@Override
	public ResultSequence constructor(ResultSequence arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new();

		if (arg.empty())
			return rs;

		AnyType aat = arg.first();
		
		if (aat instanceof XSGDay) {
			throw new DynamicError(TypeError.invalid_type(null));
		}
		
		if (!(aat.string_type().equals("xs:string") || aat instanceof NodeType ||
			aat.string_type().equals("xs:untypedAtomic") ||
			aat.string_type().equals("xs:boolean") ||
			aat instanceof NumericType)) {
			throw DynamicError.cant_cast(null);
		}
		

		try {
			Float f = null;
			if (aat.string_value().equals("INF")) {
				f = Float.POSITIVE_INFINITY;
			} else if (aat.string_value().equals("-INF")) {
				f = Float.NEGATIVE_INFINITY;
			} else if (aat instanceof XSBoolean) {
				if (aat.string_value().equals("true")) {
					f = new Float("1.0E0");
				} else {
					f = new Float("0.0E0");
				}
			} else {
			    f = new Float(aat.string_value());
			}
			rs.add(new XSFloat(f.floatValue()));
			return rs;
		} catch (NumberFormatException e) {
			throw DynamicError.cant_cast(null);
		}

	}

	/**
	 * Retrieves the actual float value stored
	 * 
	 * @return The actual float value stored
	 */
	public float float_value() {
		return _value;
	}

	/**
	 * Equality comparison between this number and the supplied representation.
	 * @param aa
	 *            The datatype to compare with
	 * 
	 * @return True if the two representations are of the same number. False
	 *         otherwise
	 * @throws DynamicError
	 */
	public boolean eq(AnyType aa, DynamicContext context) throws DynamicError {
		AnyType carg = convertArg(aa);
		if (!(carg instanceof XSFloat))
			DynamicError.throw_type_error();

		XSFloat f = (XSFloat) carg;

		return float_value() == f.float_value();
	}

	/**
	 * Comparison between this number and the supplied representation. 
	 * 
	 * @param arg
	 *            The datatype to compare with
	 * @return True if the supplied representation is a smaller number than the
	 *         one stored. False otherwise
	 * @throws DynamicError
	 */
	public boolean gt(AnyType arg, DynamicContext context) throws DynamicError {
		AnyType carg = convertArg(arg);
		XSFloat val = (XSFloat) get_single_type(carg, XSFloat.class);
		return float_value() > val.float_value();
	}

	/**
	 * Comparison between this number and the supplied representation. 
	 * 
	 * @param arg
	 *            The datatype to compare with
	 * @return True if the supplied representation is a greater number than the
	 *         one stored. False otherwise
	 * @throws DynamicError
	 */
	public boolean lt(AnyType arg, DynamicContext context) throws DynamicError {
		AnyType carg = convertArg(arg);
		XSFloat val = (XSFloat) get_single_type(carg, XSFloat.class);
		return float_value() < val.float_value();
	}

	/**
	 * Mathematical addition operator between this XSFloat and the supplied
	 * ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform an addition with
	 * @return A XSFloat consisting of the result of the mathematical addition.
	 */
	public ResultSequence plus(ResultSequence arg) throws DynamicError {
		ResultSequence carg = convertResultSequence(arg);
		AnyType at = get_single_arg(carg);
		if (!(at instanceof XSFloat))
			DynamicError.throw_type_error();
		XSFloat val = (XSFloat) at;

		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				+ val.float_value()));
	}

	/**
	 * Mathematical subtraction operator between this XSFloat and the supplied
	 * ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform a subtraction with
	 * @return A XSFloat consisting of the result of the mathematical
	 *         subtraction.
	 */
	public ResultSequence minus(ResultSequence arg) throws DynamicError {
		ResultSequence carg = constructor(arg);
		AnyType at = get_single_arg(carg);
		if (!(at instanceof XSFloat))
			DynamicError.throw_type_error();
		XSFloat val = (XSFloat) at;

		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				- val.float_value()));
	}

	/**
	 * Mathematical multiplication operator between this XSFloat and the
	 * supplied ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform a multiplication with
	 * @return A XSFloat consisting of the result of the mathematical
	 *         multiplication.
	 */
	public ResultSequence times(ResultSequence arg) throws DynamicError {
		ResultSequence carg = constructor(arg);
		XSFloat val = (XSFloat) get_single_type(carg, XSFloat.class);
		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				* val.float_value()));
	}

	/**
	 * Mathematical division operator between this XSFloat and the supplied
	 * ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform a division with
	 * @return A XSFloat consisting of the result of the mathematical division.
	 */
	public ResultSequence div(ResultSequence arg) throws DynamicError {
		ResultSequence carg = convertResultSequence(arg);
		XSFloat val = (XSFloat) get_single_type(carg, XSFloat.class);
		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				/ val.float_value()));
	}

	/**
	 * Mathematical integer division operator between this XSFloat and the
	 * supplied ResultSequence. 
	 * 
	 * @param arg
	 *            The ResultSequence to perform an integer division with
	 * @return A XSInteger consisting of the result of the mathematical integer
	 *         division.
	 */
	public ResultSequence idiv(ResultSequence arg) throws DynamicError {
		ResultSequence carg = convertResultSequence(arg);
		XSFloat val = (XSFloat) get_single_type(carg, XSFloat.class);

		if (this.nan() || val.nan())
			throw DynamicError.numeric_overflow("Dividend or divisor is NaN");

		if (this.infinite())
			throw DynamicError.numeric_overflow("Dividend is infinite");

		if (val.zero())
			throw DynamicError.div_zero(null);

		BigDecimal result = BigDecimal.valueOf((float_value() / val.float_value()));
		return ResultSequenceFactory.create_new(new XSInteger(result.toBigInteger()));
	}

	/**
	 * Mathematical modulus operator between this XSFloat and the supplied
	 * ResultSequence. Due to no numeric type promotion or conversion, the
	 * ResultSequence must be of type XSFloat.
	 * 
	 * @param arg
	 *            The ResultSequence to perform a modulus with
	 * @return A XSFloat consisting of the result of the mathematical modulus.
	 */
	public ResultSequence mod(ResultSequence arg) throws DynamicError {
		ResultSequence carg = convertResultSequence(arg);
		XSFloat val = (XSFloat) get_single_type(carg, XSFloat.class);
		return ResultSequenceFactory.create_new(new XSFloat(float_value()
				% val.float_value()));
	}

	/**
	 * Negates the number stored
	 * 
	 * @return A XSFloat representing the negation of the number stored
	 */
	@Override
	public ResultSequence unary_minus() {
		return ResultSequenceFactory
				.create_new(new XSFloat(-1 * float_value()));
	}

	/**
	 * Absolutes the number stored
	 * 
	 * @return A XSFloat representing the absolute value of the number stored
	 */
	@Override
	public NumericType abs() {
		return new XSFloat(Math.abs(float_value()));
	}

	/**
	 * Returns the smallest integer greater than the number stored
	 * 
	 * @return A XSFloat representing the smallest integer greater than the
	 *         number stored
	 */
	@Override
	public NumericType ceiling() {
		return new XSFloat((float) Math.ceil(float_value()));
	}

	/**
	 * Returns the largest integer smaller than the number stored
	 * 
	 * @return A XSFloat representing the largest integer smaller than the
	 *         number stored
	 */
	@Override
	public NumericType floor() {
		return new XSFloat((float) Math.floor(float_value()));
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSFloat representing the closest long of the number stored.
	 */
	@Override
	public NumericType round() {
		BigDecimal value = new BigDecimal(float_value());
		BigDecimal round = value.setScale(0, RoundingMode.HALF_UP);
		return new XSFloat(round.floatValue());
	}

	/**
	 * Returns the closest integer of the number stored.
	 * 
	 * @return A XSFloat representing the closest long of the number stored.
	 */
	@Override
	public NumericType round_half_to_even() {
		return round_half_to_even(0);
	}

	/**
	 * Returns the closest integer of the number stored with the specified precision.
	 * 
	 * @param precision An integer precision 
	 * @return A XSFloat representing the closest long of the number stored.
	 */

	@Override
	public NumericType round_half_to_even(int precision) {
		BigDecimal value = new BigDecimal(_value);
		BigDecimal round = value.setScale(precision, RoundingMode.HALF_EVEN);
		return new XSFloat(round.floatValue());
	}
	
	protected AnyType convertArg(AnyType arg) throws DynamicError {
		ResultSequence rs = ResultSequenceFactory.create_new(arg);
		rs = constructor(rs);
		AnyType carg = rs.first();
		return carg;
	}
	
	private ResultSequence convertResultSequence(ResultSequence arg)
			throws DynamicError {
		ResultSequence carg = arg;
		Iterator it = carg.iterator();
		while (it.hasNext()) {
			AnyType type = (AnyType) it.next();
			if (type.string_type().equals("xs:untypedAtomic") ||
				type.string_type().equals("xs:string")) {
				throw DynamicError.throw_type_error();
			}
		}

		carg = constructor(carg);
		return carg;
	}	
	
}
