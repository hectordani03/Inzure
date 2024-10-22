package io.inzure.app.ui.views

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

fun getTermsAndConditionsText(): AnnotatedString {
    return buildAnnotatedString {

        // Título principal
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp)) {
            append("Términos y Condiciones\n\n")
        }

        // Sección 1
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("1. Aceptación de los términos\n")
        }
        append("Al acceder, descargar o utilizar la aplicación móvil desarrollada por nuestra empresa, el usuario acepta explícitamente regirse por estos Términos y Condiciones, los cuales constituyen un acuerdo vinculante entre el usuario y la empresa desarrolladora. Si el usuario no está de acuerdo con la totalidad o parte de estos términos, deberá desinstalar la aplicación de inmediato y abstenerse de utilizarla. El uso continuado de la aplicación implica la aceptación de las modificaciones que se puedan realizar a estos términos.")

        // Sección 2
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n2. Descripción del servicio\n")
        }
        append("La aplicación proporciona una plataforma centralizada para la administración y gestión de seguros de vida, profesionales y automovilísticos, facilitando la interacción y comunicación entre las aseguradoras y sus clientes. La empresa no actúa como intermediaria en la contratación de seguros ni garantiza la calidad de los servicios proporcionados por las aseguradoras, limitando su función a la provisión de una herramienta digital. El usuario reconoce y acepta que el uso de la aplicación no establece ninguna relación contractual directa entre las partes sobre los productos aseguradores gestionados a través de la plataforma.")

        // Sección 3
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n3. Uso de la aplicación\n")
        }
        append("El usuario garantiza que tiene al menos 18 años de edad y que posee la capacidad legal para utilizar la aplicación conforme a la legislación vigente. Se compromete a proporcionar información veraz, actualizada y completa durante el proceso de registro y uso de la aplicación. Cualquier uso no autorizado, ilegal o en violación de estos términos por parte del usuario, incluyendo la distribución no autorizada de la aplicación o su contenido, será considerado un incumplimiento grave, lo que puede derivar en la suspensión o cancelación inmediata del acceso a la aplicación, sin perjuicio de las acciones legales correspondientes.")

        // Sección 4
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n4. Propiedad intelectual\n")
        }
        append("Todos los derechos, títulos e intereses relacionados con la aplicación, incluyendo, sin limitarse a, el software, código fuente, diseño, gráficos, textos y otros elementos de contenido, están protegidos por las leyes de propiedad intelectual y son de exclusiva propiedad de la empresa desarrolladora. El usuario se compromete a no reproducir, modificar, distribuir o explotar de manera alguna cualquier parte de la aplicación sin el consentimiento previo y por escrito de la empresa. El uso no autorizado de la propiedad intelectual puede dar lugar a acciones legales por infracción de derechos de autor y otras leyes aplicables.")

        // Sección 5
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n5. Limitación de responsabilidad\n")
        }
        append("La empresa desarrolladora no garantiza que la aplicación funcionará sin interrupciones o errores, ni que cualquier defecto será corregido de manera inmediata. El usuario acepta utilizar la aplicación bajo su propio riesgo y reconoce que la empresa no será responsable por pérdidas de datos, daños directos, indirectos, incidentales, punitivos o emergentes derivados del uso de la aplicación o de la imposibilidad de utilizarla, incluidas, sin limitación, la pérdida de ingresos, la interrupción del negocio, o cualquier otro daño de naturaleza económica.")

        // Sección 6
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n6. Modificaciones a los términos\n")
        }
        append("La empresa se reserva el derecho de modificar estos Términos y Condiciones en cualquier momento. Las modificaciones entrarán en vigor inmediatamente después de su publicación dentro de la aplicación o en el sitio web oficial. Se recomienda a los usuarios revisar regularmente estos términos para mantenerse informados de cualquier cambio. El uso continuado de la aplicación tras la publicación de los términos revisados implicará la aceptación de dichos cambios por parte del usuario.")


        // Título principal
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp)) {
            append("\n\n\nPoliticas de privacidad\n\n")
        }

        // Sección 1
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("1. Recopilación de información personal\n")
        }
        append("Al utilizar nuestra aplicación, recopilamos información personal proporcionada directamente por los usuarios, que puede incluir, entre otros, nombre, dirección de correo electrónico, información de contacto y detalles de las pólizas de seguros gestionadas a través de la aplicación. También podemos recopilar automáticamente ciertos datos técnicos, como la dirección IP del dispositivo, tipo de navegador, sistema operativo y datos de uso relativos a la interacción del usuario con la aplicación. Esta información es necesaria para proporcionar el servicio y mejorar la experiencia del usuario.")

        // Sección 2
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n2. Uso de la información La información recopilada se utilizará exclusivamente para los siguientes fines:\n\n")
        }
        append("Gestionar y facilitar la administración de pólizas de seguros entre los usuarios y las aseguradoras.\n" +
                "Personalizar la experiencia del usuario en la aplicación, incluyendo la generación de notificaciones automáticas sobre vencimientos de pólizas, pagos pendientes y otra información relevante.\n" +
                "Proteger la seguridad y confidencialidad de los datos mediante la implementación de medidas de seguridad adecuadas, tales como la encriptación y la autenticación de múltiples factores, cuando sea necesario.\n" +
                "Analizar el comportamiento de uso de la aplicación con el fin de mejorar su rendimiento, funcionalidad y capacidad de respuesta.")

        // Sección 3
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n3. Compartición de información\n")
        }
        append("No vendemos, alquilamos ni compartimos información personal con terceros para sus propios fines comerciales, salvo en los casos en los que sea necesario para cumplir con una obligación legal o para prestar los servicios solicitados a través de la aplicación, tales como el intercambio de información con las aseguradoras correspondientes para la gestión de pólizas. En situaciones donde la ley lo exija, o en caso de una fusión o adquisición de la empresa, la información personal podrá ser transferida a terceros, previa notificación a los usuarios.")

        // Sección 4
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n4. Seguridad de la información\n")
        }
        append("Nos comprometemos a proteger la seguridad de la información personal mediante el uso de prácticas estándar de la industria, como la implementación de tecnologías de encriptación y firewalls, así como la autenticación de dos factores en áreas sensibles de la aplicación. Sin embargo, el usuario reconoce que ninguna medida de seguridad es infalible y, en consecuencia, no podemos garantizar la absoluta protección de la información en todos los casos, especialmente ante ataques cibernéticos o accesos no autorizados.")

        // Sección 5
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n5. Derechos del usuario \n")
        }
        append("5. Conforme a la legislación aplicable en materia de protección de datos, el usuario tiene derecho a acceder, corregir, eliminar o restringir el procesamiento de su información personal almacenada en la aplicación. Para ejercer estos derechos, el usuario puede ponerse en contacto con el equipo de soporte a través de los medios indicados en la aplicación. El usuario también tiene derecho a oponerse al procesamiento de su información en ciertas circunstancias, así como a solicitar la portabilidad de sus datos personales.")

        // Sección 6
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("\n\n6. Modificaciones a la política de privacidad\n")
        }
        append("Nos reservamos el derecho de actualizar esta política de privacidad para reflejar cambios en nuestras prácticas de procesamiento de datos, la evolución de la legislación aplicable o la mejora de la aplicación. Las actualizaciones serán notificadas a los usuarios a través de la aplicación y se recomienda revisar regularmente la política para mantenerse informado de los cambios. El uso continuado de la aplicación tras la publicación de la política revisada implicará la aceptación de dichos cambios por parte del usuario.")
    }
}
