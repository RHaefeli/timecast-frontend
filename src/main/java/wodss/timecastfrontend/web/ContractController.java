package wodss.timecastfrontend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import wodss.timecastfrontend.domain.Contract;
import wodss.timecastfrontend.domain.Employee;
import wodss.timecastfrontend.domain.Token;
import wodss.timecastfrontend.exceptions.TimecastPreconditionFailedException;
import wodss.timecastfrontend.services.ContractService;
import wodss.timecastfrontend.services.mocks.MockContractService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/contracts")
public class ContractController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping
    public String getAll(Model model) {
        logger.debug("Get all contracts");
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Contract> contracts = contractService.getAll(new Token(token));
        model.addAttribute("contracts", contracts);

        return "contracts/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Model model) {
        logger.debug("Get contract by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Contract contract = contractService.getById(new Token(token), id);
        List<Contract> contracts = contractService.getByEmployee(new Token(token), contract.getEmployee());

        model.addAttribute("contract", contract);
        model.addAttribute("contracts", contracts);

        return "contracts/update";
    }

    @PutMapping("/{id}")
    public String updateById(@PathVariable Long id, @Valid @ModelAttribute("contract") Contract contract,
                             BindingResult bindingResult, Model model) {
        // TODO: prevent update if there exists allocations with that contract.

        logger.debug("Update contract by id: " + id);
        if (bindingResult.hasErrors()) {
            logger.debug("Binding error: " + bindingResult.getAllErrors());
            return "contracts/update";
        }
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Contract updatedContract = contractService.update(new Token(token), contract);
            List<Contract> contracts = contractService.getByEmployee(new Token(token), updatedContract.getEmployee());
            model.addAttribute("contract", updatedContract);
            model.addAttribute("contracts", contracts);
            model.addAttribute("success", "Successfully updated Contract.");
            return "contracts/update";
        } catch (TimecastPreconditionFailedException ex) {
            model.addAttribute("exception", ex.getMessage());
            return "contracts/update";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.debug("Delete contract by id: " + id);
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        contractService.deleteById(new Token(token), id);

        redirectAttributes.addFlashAttribute("success", "Successfully deleted Contract.");
        return "redirect:/contracts";
    }
}
