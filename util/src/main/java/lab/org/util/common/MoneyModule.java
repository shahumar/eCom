package lab.org.util.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import lab.org.api.common.Money;

import java.io.IOException;

public class MoneyModule extends SimpleModule {

    class MoneyDeserializer extends StdScalarDeserializer<Money> {
        protected MoneyDeserializer() {
            super(Money.class);
        }

        @Override
        public Money deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonToken token = p.getCurrentToken();
            if (token == JsonToken.VALUE_STRING) {
                String str = p.getText().trim();
                if (str.isEmpty())
                    return null;
                else
                    return new Money(str);
            }
            return (Money) ctxt.handleUnexpectedToken(handledType(), p);

        }
    }

    class MoneySerializer extends StdScalarSerializer<Money> {

        public MoneySerializer() {
            super(Money.class);
        }

        @Override
        public void serialize(Money value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.asString());

        }
    }

    @Override
    public String getModuleName() {
        return "EcomCommonModule";
    }

    public MoneyModule() {
        addSerializer(Money.class, new MoneySerializer());
        addDeserializer(Money.class, new MoneyDeserializer());
    }
}
