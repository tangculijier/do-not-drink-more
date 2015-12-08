package com.huang.util;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import com.huang.model.Phone;

/**  
 * ����������������util
 * @author lizheHuang 
 * @Date   time :2015��12��8��  ����5:20:58
 * @version 1.0
 */

public class AscNameComparator implements Comparator<Phone> {
    @Override
    public int compare(Phone p1, Phone p2) {
        String one = p1.getUserName();
        String two = p2.getUserName();
        Collator ca = Collator.getInstance(Locale.CHINA);
        int flags = 0;
        if (ca.compare(one, two) < 0) {
            flags = -1;
        } else if (ca.compare(one, two) > 0) {
            flags = 1;
        } else {
            flags = 0;
        }
        return flags;
    }
}