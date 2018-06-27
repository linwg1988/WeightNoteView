package www.linwg.org;

public interface Rule {
    CustomFormat convert(CharSequence charSequence, int rowIndex, int columnIndex);
}
