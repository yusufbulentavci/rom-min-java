package com.bilgidoku.rom.base.java.min.json;

import com.bilgidoku.rom.base.min.err.KnownError;

/*
 Copyright (c) 2006 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

/**
 * JSONWriter provides a quick and convenient way of producing JSON text. The
 * texts produced strictly conform to JSON syntax rules. No whitespace is added,
 * so the results are ready for transmission or storage. Each instance of
 * JSONWriter can produce one JSON text.
 * <p>
 * A JSONWriter instance provides a <code>value</code> method for appending
 * values to the text, and a <code>key</code> method for adding keys before
 * values in objects. There are <code>array</code> and <code>endArray</code>
 * methods that make and bound array values, and <code>object</code> and
 * <code>endObject</code> methods which make and bound object values. All of
 * these methods return the JSONWriter instance, permitting a cascade style. For
 * example,
 * 
 * <pre>
 * new JSONWriter(myWriter).object().key(&quot;JSON&quot;).value(&quot;Hello, World!&quot;).endObject();
 * </pre>
 * 
 * which writes
 * 
 * <pre>
 * {"JSON":"Hello, World!"}
 * </pre>
 * <p>
 * The first method called must be <code>array</code> or <code>object</code>.
 * There are no methods for adding commas or colons. JSONWriter adds them for
 * you. Objects and arrays can be nested up to 20 levels deep.
 * <p>
 * This can sometimes be easier than using a JSONObject to build a string.
 * 
 * @author JSON.org
 * @version 2010-12-24
 */
public class JSONWriter {
	private static final int maxdepth = 20;

	/**
	 * The comma flag determines if a comma should be output before the next
	 * value.
	 */
	private boolean comma;

	/**
	 * The current mode. Values: 'a' (array), 'd' (done), 'i' (initial), 'k'
	 * (key), 'o' (object).
	 */
	protected char mode;

	/**
	 * The object/array stack.
	 */
	private JSONObject stack[];

	/**
	 * The stack top index. A value of 0 indicates that the stack is empty.
	 */
	private int top;

	/**
	 * The writer that will receive the output.
	 */
	protected StringBuilder writer;

	/**
	 * Make a fresh JSONWriter. It can be used to build one JSON text.
	 */
	public JSONWriter(StringBuilder w) {
		this.comma = false;
		this.mode = 'i';
		this.stack = new JSONObject[maxdepth];
		this.top = 0;
		this.writer = w;
	}

	/**
	 * Append a value.
	 * 
	 * @param string
	 *          A string value.
	 * @return this
	 * @throws KnownError
	 *           If the value is out of sequence.
	 */
	private JSONWriter append(String string) throws KnownError {
		if (string == null) {
			throw new KnownError("Null pointer");
		}
		if (this.mode == 'o' || this.mode == 'a') {
			if (this.comma && this.mode == 'a') {
				this.writer.append(',');
			}
			this.writer.append(string);
			if (this.mode == 'o') {
				this.mode = 'k';
			}
			this.comma = true;
			return this;
		}
		throw new KnownError("Value out of sequence.");
	}

	/**
	 * Begin appending a new array. All values until the balancing
	 * <code>endArray</code> will be appended to this array. The
	 * <code>endArray</code> method must be called to mark the array's end.
	 * 
	 * @return this
	 * @throws KnownError
	 *           If the nesting is too deep, or if the object is started in the
	 *           wrong place (for example as a key or after the end of the
	 *           outermost array or object).
	 */
	public JSONWriter array() throws KnownError {
		if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
			this.push(null);
			this.append("[");
			this.comma = false;
			return this;
		}
		throw new KnownError("Misplaced array.");
	}

	/**
	 * End something.
	 * 
	 * @param mode
	 *          Mode
	 * @param c
	 *          Closing character
	 * @return this
	 * @throws KnownError
	 *           If unbalanced.
	 */
	private JSONWriter end(char mode, char c) throws KnownError {
		if (this.mode != mode) {
			throw new KnownError(mode == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
		}
		this.pop(mode);
		this.writer.append(c);
		this.comma = true;
		return this;
	}

	/**
	 * End an array. This method most be called to balance calls to
	 * <code>array</code>.
	 * 
	 * @return this
	 * @throws KnownError
	 *           If incorrectly nested.
	 */
	public JSONWriter endArray() throws KnownError {
		return this.end('a', ']');
	}

	/**
	 * End an object. This method most be called to balance calls to
	 * <code>object</code>.
	 * 
	 * @return this
	 * @throws KnownError
	 *           If incorrectly nested.
	 */
	public JSONWriter endObject() throws KnownError {
		return this.end('k', '}');
	}

	/**
	 * Append a key. The key will be associated with the next value. In an object,
	 * every value must be preceded by a key.
	 * 
	 * @param string
	 *          A key string.
	 * @return this
	 * @throws KnownError
	 *           If the key is out of place. For example, keys do not belong in
	 *           arrays or if the key is null.
	 */
	public JSONWriter key(String string) throws KnownError {
		if (string == null) {
			throw new KnownError("Null key.");
		}
		if (this.mode == 'k') {
			stack[top - 1].putOnce(string, Boolean.TRUE);
			if (this.comma) {
				this.writer.append(',');
			}
			this.writer.append(JSONObject.quote(string));
			this.writer.append(':');
			this.comma = false;
			this.mode = 'o';
			return this;

		}
		throw new KnownError("Misplaced key.");
	}

	/**
	 * Begin appending a new object. All keys and values until the balancing
	 * <code>endObject</code> will be appended to this object. The
	 * <code>endObject</code> method must be called to mark the object's end.
	 * 
	 * @return this
	 * @throws KnownError
	 *           If the nesting is too deep, or if the object is started in the
	 *           wrong place (for example as a key or after the end of the
	 *           outermost array or object).
	 */
	public JSONWriter object() throws KnownError {
		if (this.mode == 'i') {
			this.mode = 'o';
		}
		if (this.mode == 'o' || this.mode == 'a') {
			this.append("{");
			this.push(new JSONObject());
			this.comma = false;
			return this;
		}
		throw new KnownError("Misplaced object.");

	}

	/**
	 * Pop an array or object scope.
	 * 
	 * @param c
	 *          The scope to close.
	 * @throws KnownError
	 *           If nesting is wrong.
	 */
	private void pop(char c) throws KnownError {
		if (this.top <= 0) {
			throw new KnownError("Nesting error.");
		}
		char m = this.stack[this.top - 1] == null ? 'a' : 'k';
		if (m != c) {
			throw new KnownError("Nesting error.");
		}
		this.top -= 1;
		this.mode = this.top == 0 ? 'd' : this.stack[this.top - 1] == null ? 'a' : 'k';
	}

	/**
	 * Push an array or object scope.
	 * 
	 * @param cs
	 *          The scope to open.
	 * @throws KnownError
	 *           If nesting is too deep.
	 */
	private void push(JSONObject jo) throws KnownError {
		if (this.top >= maxdepth) {
			throw new KnownError("Nesting too deep.");
		}
		this.stack[this.top] = jo;
		this.mode = jo == null ? 'a' : 'k';
		this.top += 1;
	}

	/**
	 * Append either the value <code>true</code> or the value <code>false</code>.
	 * 
	 * @param b
	 *          A boolean.
	 * @return this
	 * @throws KnownError
	 */
	public JSONWriter value(boolean b) throws KnownError {
		return this.append(b ? "true" : "false");
	}

	/**
	 * Append a double value.
	 * 
	 * @param d
	 *          A double.
	 * @return this
	 * @throws KnownError
	 *           If the number is not finite.
	 */
	public JSONWriter value(double d) throws KnownError {
		return this.value(new Double(d));
	}

	/**
	 * Append a long value.
	 * 
	 * @param l
	 *          A long.
	 * @return this
	 * @throws KnownError
	 */
	public JSONWriter value(long l) throws KnownError {
		return this.append(Long.toString(l));
	}

	/**
	 * Append an object value.
	 * 
	 * @param object
	 *          The object to append. It can be null, or a Boolean, Number,
	 *          String, JSONObject, or JSONArray, or an object that implements
	 *          JSONString.
	 * @return this
	 * @throws KnownError
	 *           If the value is out of sequence.
	 */
	public JSONWriter value(Object object) throws KnownError {
		return this.append(JSONObject.valueToString(object));
	}
}
