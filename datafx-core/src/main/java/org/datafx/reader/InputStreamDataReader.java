/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datafx.reader;

import java.io.InputStream;
import org.datafx.reader.util.InputStreamConverter;

/**
 *
 * @author johan
 */
public abstract class InputStreamDataReader<T> extends AbstractDataReader<T> {

    private InputStream is;
    private InputStreamConverter<T> converter;

    public void setInputStream(InputStream is) {
        this.is = is;
    }

    public T getData() {
        if (isSingle()) {
            T answer = converter.convert(is);
            return answer;
        } else {
            T answer = converter.next(is);
            return answer;
        }
    }

    public boolean hasMoreData() {
        return converter.hasMoreData(is);
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter(InputStreamConverter<T> converter) {
        this.converter = converter;
    }
}