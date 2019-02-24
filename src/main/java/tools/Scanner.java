/*
 * Copyright (C) 2019 Stuart Davies (stuartdd)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools;

import exceptions.BadDataException;

public class Scanner {

    private final char[] buffer;
    private final int max;

    private int pos;

    public Scanner(final String input) {
        this.buffer = input.toCharArray();
        this.pos = 0;
        this.max = this.buffer.length;
    }

    public char next() {
        if (pos < max) {
            return buffer[pos++];
        }
        return 0;
    }

    public boolean hasNext() {
        return (pos < max);
    }

    public int back() {
        if (pos > 0) {
            pos--;
        }
        return pos;
    }

    public void skipSpace() {
        while (hasNext()) {
            if (next() > ' ') {
                back();
                return;
            }
        }
    }

    public void skipToNext(final char c) {
        while (hasNext()) {
            if (next() == c) {
                back();
                return;
            }
        }
    }

    public String nextToken(String set) {
        StringBuilder sb = new StringBuilder();
        skipSpace();
        while (hasNext()) {
            char c = next();
            if (c < ' ') {
                c = ' ';
            }
            if (isIn(c, set)) {
                return sb.toString().trim();
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    public boolean isIn(char needle, String hayStack) {
        for (char c : hayStack.toCharArray()) {
            if (needle == c) {
                return true;
            }
        }
        return false;
    }

    public String scanQuotedString(final char delim) {
        StringBuilder sb = new StringBuilder();
        while (hasNext()) {
            char c = next();
            if (c == '\\') {
                c = next();
                if (c == '\\') {
                    sb.append('\\');
                } else {
                    sb.append(c);
                }
            } else {
                if (c == delim) {
                    return sb.toString();
                }
                if (hasNext()) {
                    if (buffer[pos] == delim) {
                        next();
                        sb.append(c);
                        return sb.toString();
                    }
                } else {
                    throw new BadDataException("Unterminated quoted String");
                }
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int from = pos - 40;
        if (from < 0) {
            from = 0;
        }
        int p = from;
        while ((sb.length() < 60) && (p < buffer.length)) {
            if (p == pos) {
                sb.append('|');
            }
            if (buffer[p] < ' ') {
                sb.append(' ');
            } else {
                sb.append(buffer[p]);
            }
            if (p == pos) {
                sb.append('|');
            }
            p++;
        }
        return "Scanner len[" + buffer.length + "] pos[" + pos + "] " + (pos < max ? "next[" + buffer[pos] + "]" : "") + "-->" + sb + "<--";
    }

}
