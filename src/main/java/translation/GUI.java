package translation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GUI for the Country Name Translator program.
 * Dropdown menus show country and language names,
 * but translation uses their codes behind the scenes.
 */
public class GUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- Converters ---
            CountryCodeConverter countryConverter = new CountryCodeConverter();
            LanguageCodeConverter languageConverter = new LanguageCodeConverter();

            // --- Translator with JSON data ---
            Translator translator = new JSONTranslator();

            // Fetch available codes
            List<String> countryCodes = translator.getCountryCodes();
            List<String> languageCodes = translator.getLanguageCodes();

            // --- Country dropdown (shows names) ---
            JPanel countryPanel = new JPanel();
            JComboBox<String> countryCombo = new JComboBox<>();
            for (String code : countryCodes) {
                String name = countryConverter.fromCountryCode(code);
                countryCombo.addItem(name != null ? name : code); // fallback to code if missing
            }
//            countryPanel.add(new JLabel("Country:"));
            countryPanel.add(countryCombo);

            // --- Language dropdown (shows names) ---
            JPanel languagePanel = new JPanel();
            JComboBox<String> languageCombo = new JComboBox<>();
            for (String code : languageCodes) {
                String name = languageConverter.fromLanguageCode(code);
                languageCombo.addItem(name != null ? name : code);
            }
            languagePanel.add(new JLabel("Language:"));
            languagePanel.add(languageCombo);

            // --- Result label ---
            JPanel resultPanel = new JPanel();
            JLabel resultLabelText = new JLabel("Translated:");
            JLabel resultLabel = new JLabel("Select country and language.");
            resultPanel.add(resultLabelText);
            resultPanel.add(resultLabel);

            // --- Action listener to update translation ---
            ActionListener updateTranslation = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedCountryName = (String) countryCombo.getSelectedItem();
                    String selectedLanguageName = (String) languageCombo.getSelectedItem();

                    if (selectedCountryName != null && selectedLanguageName != null) {
                        String countryCode = countryConverter.fromCountry(selectedCountryName);
                        String languageCode = languageConverter.fromLanguage(selectedLanguageName);

                        String translation = translator.translate(countryCode, languageCode);
                        if (translation == null) {
                            translation = "No translation found!";
                        }
                        resultLabel.setText(translation);
                    }
                }
            };

            // Attach listener to both dropdowns
            countryCombo.addActionListener(updateTranslation);
            languageCombo.addActionListener(updateTranslation);

            // --- Main layout ---
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(languagePanel);
            mainPanel.add(resultPanel);
            mainPanel.add(countryPanel);

            JFrame frame = new JFrame("Country Name Translator");
            frame.setContentPane(mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

            // Trigger an initial translation for defaults
            if (!countryCodes.isEmpty() && !languageCodes.isEmpty()) {
                countryCombo.setSelectedIndex(0);
                languageCombo.setSelectedIndex(0);
            }
        });
    }
}
